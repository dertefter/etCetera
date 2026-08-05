package com.dertefter.data.repository

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.feed.PollDto
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.dto.feed.stats.PostStatsDto
import com.dertefter.data.dto.new_post.EditPostRequestDto
import com.dertefter.data.dto.new_post.EditPostResponseDto
import com.dertefter.data.dto.new_post.NewPostRequestDto
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.extension.find
import com.jamal_aliev.paginator.cursor.extension.prependElement
import com.jamal_aliev.paginator.cursor.extension.removeAll
import com.jamal_aliev.paginator.cursor.extension.updateWhere
import kotlinx.coroutines.flow.Flow
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : PostRepository {

    private val activePaginators = CopyOnWriteArrayList<WeakReference<MutableCursorPaginator<String, PostDto>>>()

    override fun registerPaginator(paginator: MutableCursorPaginator<String, PostDto>) {
        activePaginators.add(WeakReference(paginator))
    }

    private suspend fun updateData(predicate: (PostDto) -> Boolean, transform: (PostDto) -> PostDto) {
        activePaginators.removeIf { it.get() == null }
        activePaginators.forEach { ref ->
            ref.get()?.let { paginator ->
                paginator.updateWhere(predicate = predicate, transform = transform)
                paginator.flush()
            }
        }
        updatePagesInDb(predicate, transform)
    }

    private suspend fun updatePagesInDb(predicate: (PostDto) -> Boolean, transform: (PostDto) -> PostDto) {
        val allPosts = localDataSource.getAllPosts()
        allPosts.forEach { post ->
            if (predicate(post)) {
                localDataSource.savePost(transform(post))
            }
        }
    }

    override suspend fun updatePostStats(ids: List<String>): Result<List<PostStatsDto>> {
        return runCatching {
            val statsList = remoteDataSource.getStats(ids).getOrThrow()
            val statsMap = statsList.associateBy { it.id }

            val predicate: (PostDto) -> Boolean = { statsMap.containsKey(it.id) }
            val transform: (PostDto) -> PostDto = { post ->
                statsMap[post.id]?.let { stats ->
                    post.copy(
                        likesCount = stats.likesCount,
                        commentsCount = stats.commentsCount,
                        repostsCount = stats.repostsCount,
                        viewsCount = stats.viewsCount,
                        dominantEmoji = stats.dominantEmoji
                    )
                } ?: post
            }

            updateData(predicate, transform)
            statsList
        }
    }

    override suspend fun likePost(postId: String): Result<LikeResponseDto> {
        applyOptimisticLike(postId, true)
        return remoteDataSource.likePost(postId).onSuccess { response ->
            updateData(
                predicate = { it.id == postId },
                transform = { it.copy(isLiked = response.liked, likesCount = response.likesCount) }
            )
        }.onFailure {
            applyOptimisticLike(postId, false)
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return remoteDataSource.deletePost(postId).onSuccess {
            activePaginators.removeIf { it.get() == null }
            activePaginators.forEach { ref ->
                ref.get()?.let { paginator ->
                    paginator.removeAll { it.id == postId }
                    paginator.flush()
                }
            }
            localDataSource.deletePost(postId)
        }
    }

    override suspend fun unlikePost(postId: String): Result<LikeResponseDto> {
        applyOptimisticLike(postId, false)
        return remoteDataSource.unlikePost(postId).onSuccess { response ->
            updateData(
                predicate = { it.id == postId },
                transform = { it.copy(isLiked = response.liked, likesCount = response.likesCount) }
            )
        }.onFailure {
            applyOptimisticLike(postId, true)
        }
    }

    override suspend fun pinPost(postId: String): Result<Unit> {
        return remoteDataSource.pinPost(postId).onSuccess {
            activePaginators.removeIf { it.get() == null }
            activePaginators.forEach { ref ->
                ref.get()?.let { paginator ->
                    val post = paginator.find { it.id == postId }
                    if (post != null) {
                        paginator.removeAll { it.id == postId }
                        paginator.prependElement(post.copy(isPinned = true))
                        paginator.flush()
                    }
                }
            }
        }
    }

    override suspend fun unpinPost(postId: String): Result<Unit> {
        return remoteDataSource.unpinPost(postId).onSuccess {
            updateData(
                predicate = { it.id == postId },
                transform = { it.copy(isPinned = false) }
            )
        }
    }

    override suspend fun votePoll(postId: String, optionIds: List<String>): Result<PollDto> {
        return remoteDataSource.vote(postId, optionIds).onSuccess { poll ->
            updateData(
                predicate = { it.id == postId },
                transform = { it.copy(poll = poll) }
            )
        }
    }

    override fun getPost(postId: String): Flow<PostDto?> {
        return localDataSource.getPost(postId)
    }

    override suspend fun updatePost(postId: String): Result<PostDto> {
        return remoteDataSource.getPost(postId).onSuccess { postDto ->
            localDataSource.savePost(postDto)
            updateData(
                predicate = { it.id == postId },
                transform = { postDto }
            )
        }
    }

    override suspend fun newPost(newPostRequestDto: NewPostRequestDto): Result<PostDto> {
        return remoteDataSource.newPost(newPostRequestDto).onSuccess { postDto ->
            localDataSource.savePost(postDto)
            activePaginators.removeIf { it.get() == null }
            activePaginators.forEach { ref ->
                ref.get()?.let { paginator ->
                    paginator.prependElement(postDto)
                    paginator.flush()
                }
            }
        }
    }

    override suspend fun repost(postId: String, newPostRequestDto: NewPostRequestDto): Result<PostDto> {
        return remoteDataSource.repost(postId, newPostRequestDto).onSuccess { postDto ->
            localDataSource.savePost(postDto)
            activePaginators.removeIf { it.get() == null }
            activePaginators.forEach { ref ->
                ref.get()?.let { paginator ->
                    paginator.prependElement(postDto)
                    paginator.flush()
                }
            }
        }
    }

    override suspend fun editPost(
        postId: String,
        editPostRequestDto: EditPostRequestDto
    ): Result<EditPostResponseDto> {
        return remoteDataSource.editPost(postId, editPostRequestDto).onSuccess { response ->
            updateData(
                predicate = { it.id == postId },
                transform = { post ->
                    post.copy(
                        content = response.content,
                        spans = response.spans,
                        editedAt = response.updatedAt,
                        isPinned = response.isPinned
                    )
                }
            )
        }
    }

    private suspend fun applyOptimisticLike(postId: String, liked: Boolean) {
        val predicate: (PostDto) -> Boolean = { it.id == postId }
        val transform: (PostDto) -> PostDto = { post ->
            if (post.isLiked == liked) post
            else {
                val newCount = if (liked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
                post.copy(isLiked = liked, likesCount = newCount)
            }
        }
        updateData(predicate, transform)
    }
}
