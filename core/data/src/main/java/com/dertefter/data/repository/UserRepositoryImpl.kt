package com.dertefter.data.repository

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.PostPagingCache
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.dto.feed.stats.PostStatsDto
import com.dertefter.data.dto.user.FollowResponseDto
import com.dertefter.data.dto.user.UserDto
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.cache.eviction.CursorMostRecentPagingCache
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.extension.updateWhere
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val followersRepository: FollowersRepository
) : UserRepository {

    private val activePaginators = CopyOnWriteArrayList<WeakReference<MutableCursorPaginator<String, PostDto>>>()

    private suspend fun updateInMem(predicate: (PostDto) -> Boolean, transform: (PostDto) -> PostDto) {
        activePaginators.removeIf { it.get() == null }
        activePaginators.forEach { ref ->
            ref.get()?.let { paginator ->
                paginator.updateWhere(predicate = predicate, transform = transform)
                paginator.flush()
            }
        }
    }

    override fun getUser(userId: String): Flow<UserDto?> {
        return localDataSource.getUser(userId)
    }

    override suspend fun updateUser(userId: String): Result<UserDto> {
        return remoteDataSource.getUser(userId).onSuccess {
            localDataSource.saveUser(it)
        }
    }

    override fun getPostsPaginator(userId: String, sort: String, pinnedPostId: String?): MutableCursorPaginator<String, PostDto> {
        val cacheKey = "user_$userId" + "_$sort"
        return mutableCursorPaginator(capacity = 20) {
            cache = CursorMostRecentPagingCache(maxSize = 20)
            persistentCache = PostPagingCache(cacheKey, localDataSource)

            load { cursor ->
                val result = remoteDataSource.getPosts(
                    userId, sort = sort, pinnedPostId = pinnedPostId,
                    cursor = cursor?.self
                )
                val data = result.getOrThrow()

                CursorLoadResult(
                    data = data.posts,
                    bookmark = CursorBookmark(
                        prev = cursor?.prev,
                        self = cursor?.self ?: "head",
                        next = if (data.pagination.hasMore) data.pagination.nextCursor else null
                    )
                )
            }
            initialCursor = CursorBookmark(prev = null, self = "head", next = null)
        }.also {
            activePaginators.add(WeakReference(it))
        }
    }

    override fun getLikedPostsPaginator(userId: String): MutableCursorPaginator<String, PostDto> {
        val cacheKey = "liked_user_$userId"
        return mutableCursorPaginator(capacity = 20) {
            cache = CursorMostRecentPagingCache(maxSize = 20)
            persistentCache = PostPagingCache(cacheKey, localDataSource)

            load { cursor ->
                val result = remoteDataSource.getLikedPosts(userId, cursor?.self)
                val data = result.getOrThrow()

                CursorLoadResult(
                    data = data.posts,
                    bookmark = CursorBookmark(
                        prev = cursor?.prev,
                        self = cursor?.self ?: "head",
                        next = if (data.pagination.hasMore) data.pagination.nextCursor else null
                    )
                )
            }
            initialCursor = CursorBookmark(prev = null, self = "head", next = null)
        }.also {
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

            updatePagesInDb(predicate, transform)
            updateInMem(predicate, transform)

            statsList
        }
    }

    override suspend fun likePost(postId: String): Result<LikeResponseDto> {
        applyOptimisticLike(postId, true)
        return remoteDataSource.likePost(postId).onSuccess { response ->
            handleLikeResponse(postId, response)
        }.onFailure {
            applyOptimisticLike(postId, false)
        }
    }

    override suspend fun unlikePost(postId: String): Result<LikeResponseDto> {
        applyOptimisticLike(postId, false)
        return remoteDataSource.unlikePost(postId).onSuccess { response ->
            handleLikeResponse(postId, response)
        }.onFailure {
            applyOptimisticLike(postId, true)
        }
    }

    override suspend fun follow(userId: String): Result<FollowResponseDto> {
        applyOptimisticFollow(userId, true)
        return remoteDataSource.follow(userId).onSuccess { response ->
            handleFollowResponse(userId, response)
        }.onFailure {
            applyOptimisticFollow(userId, false)
        }
    }

    override suspend fun unfollow(userId: String): Result<FollowResponseDto> {
        applyOptimisticFollow(userId, false)
        return remoteDataSource.unfollow(userId).onSuccess { response ->
            handleFollowResponse(userId, response)
        }.onFailure {
            applyOptimisticFollow(userId, true)
        }
    }

    private suspend fun applyOptimisticFollow(userId: String, following: Boolean) {
        followersRepository.updateFollowingStatus(userId, following)
        localDataSource.getUser(userId).firstOrNull()?.let { user ->
            if (user.isFollowing != following) {
                val newCount = if (following) user.followersCount + 1 else (user.followersCount - 1).coerceAtLeast(0)
                localDataSource.saveUser(user.copy(isFollowing = following, followersCount = newCount))
            }
        }
    }

    private suspend fun handleFollowResponse(userId: String, response: FollowResponseDto) {
        followersRepository.updateFollowingStatus(userId, response.following)
        localDataSource.getUser(userId).firstOrNull()?.let { user ->
            localDataSource.saveUser(
                user.copy(
                    isFollowing = response.following,
                    followersCount = response.followersCount
                )
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
        updatePagesInDb(predicate, transform)
        updateInMem(predicate, transform)
    }

    private suspend fun handleLikeResponse(postId: String, response: LikeResponseDto) {
        val predicate: (PostDto) -> Boolean = { it.id == postId }
        val transform: (PostDto) -> PostDto = { it.copy(isLiked = response.liked, likesCount = response.likesCount) }

        updatePagesInDb(predicate, transform)
        updateInMem(predicate, transform)
    }
}
