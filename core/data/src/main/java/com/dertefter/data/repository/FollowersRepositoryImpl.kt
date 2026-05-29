package com.dertefter.data.repository

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.FollowerPagingCache
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.followers.FollowerUserDto
import com.jamal_aliev.paginator.CursorPagingCore
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cache.eviction.CursorMostRecentPagingCache
import com.jamal_aliev.paginator.extension.updateWhere
import com.jamal_aliev.paginator.load.CursorLoadResult
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FollowersRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : FollowersRepository {

    private val activePaginators = CopyOnWriteArrayList<WeakReference<MutableCursorPaginator<FollowerUserDto>>>()

    private suspend fun updateInMem(predicate: (FollowerUserDto) -> Boolean, transform: (FollowerUserDto) -> FollowerUserDto) {
        activePaginators.removeIf { it.get() == null }
        activePaginators.forEach { ref ->
            ref.get()?.let { paginator ->
                paginator.updateWhere(predicate = predicate, transform = transform)
                paginator.flush()
            }
        }
    }

    override suspend fun updateFollowingStatus(userId: String, isFollowing: Boolean) {
        updateInMem(
            predicate = { it.id == userId },
            transform = { it.copy(isFollowing = isFollowing) }
        )
    }

    override fun getFollowersPaginator(userId: String): MutableCursorPaginator<FollowerUserDto> {
        val cacheKey = "followers_$userId"
        val pagingCore = CursorPagingCore<FollowerUserDto>(
            cache = CursorMostRecentPagingCache(maxSize = 20),
            persistentCache = FollowerPagingCache(cacheKey, localDataSource)
        )
        return MutableCursorPaginator(
            core = pagingCore,
            load = { cursor ->
                val page = (cursor?.self as? String)?.toIntOrNull() ?: 1
                val result = remoteDataSource.getFollowers(userId, page)
                val data = result.getOrThrow()

                CursorLoadResult(
                    data = data.users,
                    bookmark = CursorBookmark(
                        prev = if (page > 1) (page - 1).toString() else null,
                        self = page.toString(),
                        next = if (data.pagination.hasMore) (page + 1).toString() else null
                    )
                )
            }
        ).also {
            activePaginators.add(WeakReference(it))
        }
    }

    override fun getFollowingPaginator(userId: String): MutableCursorPaginator<FollowerUserDto> {
        val cacheKey = "following_$userId"
        val pagingCore = CursorPagingCore<FollowerUserDto>(
            cache = CursorMostRecentPagingCache(maxSize = 20),
            persistentCache = FollowerPagingCache(cacheKey, localDataSource)
        )
        return MutableCursorPaginator(
            core = pagingCore,
            load = { cursor ->
                val page = (cursor?.self as? String)?.toIntOrNull() ?: 1
                val result = remoteDataSource.getFollowing(userId, page)
                val data = result.getOrThrow()

                CursorLoadResult(
                    data = data.users,
                    bookmark = CursorBookmark(
                        prev = if (page > 1) (page - 1).toString() else null,
                        self = page.toString(),
                        next = if (data.pagination.hasMore) (page + 1).toString() else null
                    )
                )
            }
        ).also {
            activePaginators.add(WeakReference(it))
        }
    }
}
