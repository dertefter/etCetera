package com.dertefter.data.repository

import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.draft.DraftDto
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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@Inject
class PostRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val crashlyticsRepository: CrashlyticsRepository,
) : PostRepository {

    /**
     * A live paginator plus the rule that decides whether a post belongs to its feed.
     * Likes, edits, deletions and stat updates apply to every registered paginator, but a
     * post may only be inserted at the head of the feeds that actually own it — a cursor
     * paginator cannot spawn a trailing page for the overflow, so inserting into a foreign
     * feed would show content that does not belong there and drop the head page's tail.
     */
    private class RegisteredPaginator(
        val paginator: WeakReference<MutableCursorPaginator<String, PostDto>>,
        val ownsPost: (PostDto) -> Boolean,
    )

    private val activePaginators = CopyOnWriteArrayList<RegisteredPaginator>()

    override fun registerPaginator(
        paginator: MutableCursorPaginator<String, PostDto>,
        ownsPost: (PostDto) -> Boolean,
    ) {
        activePaginators.add(RegisteredPaginator(WeakReference(paginator), ownsPost))
    }

    private fun forEachPaginator(action: (MutableCursorPaginator<String, PostDto>) -> Unit) {
        activePaginators.removeIf { it.paginator.get() == null }
        activePaginators.forEach { registered -> registered.paginator.get()?.let(action) }
    }

    private fun prependWhereOwned(post: PostDto) {
        activePaginators.removeIf { it.paginator.get() == null }
        activePaginators.forEach { registered ->
            val paginator = registered.paginator.get() ?: return@forEach
            if (registered.ownsPost(post)) paginator.prependElement(post)
        }
    }

    private suspend fun updateData(predicate: (PostDto) -> Boolean, transform: (PostDto) -> PostDto) {
        forEachPaginator { paginator ->
            paginator.updateWhere(predicate = predicate, transform = transform)
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
            val statsList = remoteDataSource.getStats(ids).onFailureLog(crashlyticsRepository).getOrThrow()
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
        return remoteDataSource.likePost(postId).onFailureLog(crashlyticsRepository).onSuccess { response ->
            updateData(
                predicate = { it.id == postId },
                transform = { it.copy(isLiked = response.liked, likesCount = response.likesCount) }
            )
        }.onFailure {
            applyOptimisticLike(postId, false)
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return remoteDataSource.deletePost(postId).onFailureLog(crashlyticsRepository).onSuccess {
            forEachPaginator { paginator ->
                paginator.removeAll { it.id == postId }
            }
            localDataSource.deletePost(postId)
        }
    }

    override suspend fun unlikePost(postId: String): Result<LikeResponseDto> {
        applyOptimisticLike(postId, false)
        return remoteDataSource.unlikePost(postId).onFailureLog(crashlyticsRepository).onSuccess { response ->
            updateData(
                predicate = { it.id == postId },
                transform = { it.copy(isLiked = response.liked, likesCount = response.likesCount) }
            )
        }.onFailure {
            applyOptimisticLike(postId, true)
        }
    }

    override suspend fun pinPost(postId: String): Result<Unit> {
        return remoteDataSource.pinPost(postId).onFailureLog(crashlyticsRepository).onSuccess {
            activePaginators.removeIf { it.paginator.get() == null }
            activePaginators.forEach { registered ->
                val paginator = registered.paginator.get() ?: return@forEach
                val post = paginator.find { it.id == postId } ?: return@forEach
                if (!registered.ownsPost(post)) return@forEach
                paginator.removeAll { it.id == postId }
                paginator.prependElement(post.copy(isPinned = true))
            }
        }
    }

    override suspend fun unpinPost(postId: String): Result<Unit> {
        return remoteDataSource.unpinPost(postId).onFailureLog(crashlyticsRepository).onSuccess {
            updateData(
                predicate = { it.id == postId },
                transform = { it.copy(isPinned = false) }
            )
        }
    }

    override suspend fun votePoll(postId: String, optionIds: List<String>): Result<PollDto> {
        return remoteDataSource.vote(postId, optionIds).onFailureLog(crashlyticsRepository).onSuccess { poll ->
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
        return remoteDataSource.getPost(postId).onFailureLog(crashlyticsRepository).onSuccess { postDto ->
            localDataSource.savePost(postDto)
            updateData(
                predicate = { it.id == postId },
                transform = { postDto }
            )
        }
    }

    override suspend fun newPost(newPostRequestDto: NewPostRequestDto): Result<PostDto> {
        return remoteDataSource.newPost(newPostRequestDto).onFailureLog(crashlyticsRepository).onSuccess { postDto ->
            localDataSource.savePost(postDto)
            prependWhereOwned(postDto)
        }
    }

    override suspend fun repost(postId: String, newPostRequestDto: NewPostRequestDto): Result<PostDto> {
        return remoteDataSource.repost(postId, newPostRequestDto).onFailureLog(crashlyticsRepository).onSuccess { postDto ->
            localDataSource.savePost(postDto)
            prependWhereOwned(postDto)
        }
    }

    override suspend fun editPost(
        postId: String,
        editPostRequestDto: EditPostRequestDto
    ): Result<EditPostResponseDto> {
        return remoteDataSource.editPost(postId, editPostRequestDto).onFailureLog(crashlyticsRepository).onSuccess { response ->
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

    override suspend fun savePostDraft(draftDto: DraftDto, key: String) {
        localDataSource.saveDraft(key, draftDto)
    }

    override fun getPostDraft(key: String): Flow<DraftDto> {
        return localDataSource.getDraft(key)
    }
}
