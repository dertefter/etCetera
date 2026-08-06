package com.dertefter.data.repository

import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.FollowerPagingCache
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.followers.FollowerUserDto
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.cache.eviction.CursorMostRecentPagingCache
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.extension.updateWhere
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FollowersRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val crashlyticsRepository: CrashlyticsRepository,
) : FollowersRepository {

    private val activePaginators = CopyOnWriteArrayList<WeakReference<MutableCursorPaginator<String, FollowerUserDto>>>()

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

    override fun getFollowersPaginator(userId: String): MutableCursorPaginator<String, FollowerUserDto> {
        val cacheKey = "followers_$userId"
        return mutableCursorPaginator(capacity = 20) {
            cache = CursorMostRecentPagingCache(maxSize = 20)
            persistentCache = FollowerPagingCache(cacheKey, localDataSource)

            load { cursor ->
                val page = (cursor?.self)?.toIntOrNull() ?: 1
                val result = remoteDataSource.getFollowers(userId, page).onFailureLog(crashlyticsRepository)
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
            initialCursor = CursorBookmark(prev = null, self = "1", next = null)
        }.also {
            activePaginators.add(WeakReference(it))
        }
    }

    override fun getFollowingPaginator(userId: String): MutableCursorPaginator<String, FollowerUserDto> {
        val cacheKey = "following_$userId"
        return mutableCursorPaginator(capacity = 20) {
            cache = CursorMostRecentPagingCache(maxSize = 20)
            persistentCache = FollowerPagingCache(cacheKey, localDataSource)

            load { cursor ->
                val page = (cursor?.self)?.toIntOrNull() ?: 1
                val result = remoteDataSource.getFollowing(userId, page).onFailureLog(crashlyticsRepository)
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
            initialCursor = CursorBookmark(prev = null, self = "1", next = null)
        }.also {
            activePaginators.add(WeakReference(it))
        }
    }
}
