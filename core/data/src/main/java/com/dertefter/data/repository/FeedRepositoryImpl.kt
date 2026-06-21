package com.dertefter.data.repository

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.PostPagingCache
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.feed.PollDto
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.dto.feed.stats.PostStatsDto
import com.jamal_aliev.paginator.CursorPagingCore
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cache.eviction.CursorMostRecentPagingCache
import com.jamal_aliev.paginator.extension.updateWhere
import com.jamal_aliev.paginator.load.CursorLoadResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : FeedRepository {

    private val activePaginators = CopyOnWriteArrayList<WeakReference<MutableCursorPaginator<PostDto>>>()

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

    override fun getFeedPaginator(tab: String): MutableCursorPaginator<PostDto> {
        val pagingCore = CursorPagingCore(
            cache = CursorMostRecentPagingCache(maxSize = 20),
            persistentCache = PostPagingCache(tab, localDataSource)
        )
        return MutableCursorPaginator(
            core = pagingCore,
            load = { cursor ->
                val result = remoteDataSource.getPosts(tab, cursor?.self as? String)
                val data = result.getOrThrow()

                CursorLoadResult(
                    data = data.posts,
                    bookmark = CursorBookmark(
                        prev = cursor?.prev,
                        self = cursor?.self ?: "initial",
                        next = if (data.pagination.hasMore) data.pagination.nextCursor else null
                    )
                )
            }
        ).also {
            activePaginators.add(WeakReference(it))
        }
    }

    override fun getHashtagPaginator(hashtag: String): MutableCursorPaginator<PostDto> {
        val pagingCore = CursorPagingCore(
            cache = CursorMostRecentPagingCache(maxSize = 20),
            persistentCache = PostPagingCache(hashtag, localDataSource)
        )
        return MutableCursorPaginator(
            core = pagingCore,
            load = { cursor ->
                val result = remoteDataSource.getPostsForHashtag(hashtag, cursor?.self as? String)
                val data = result.getOrThrow()

                CursorLoadResult(
                    data = data.posts,
                    bookmark = CursorBookmark(
                        prev = cursor?.prev,
                        self = cursor?.self ?: "initial",
                        next = if (data.pagination.hasMore) data.pagination.nextCursor else null
                    )
                )
            }
        ).also {
            activePaginators.add(WeakReference(it))
        }
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

    override suspend fun votePoll(postId: String, optionIds: List<String>): Result<PollDto> {
        return remoteDataSource.vote(postId, optionIds).onSuccess { poll ->
            updateData(
                predicate = { it.id == postId },
                transform = { it.copy(poll = poll) }
            )
        }
    }

    override fun getPost(postId: String): Flow<PostDto> {
        return localDataSource.getPost(postId).filterNotNull()
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
