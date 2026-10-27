package com.dertefter.data.repository

import com.dertefter.data.common.loadCursorResultWithLocalFallback
import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.entity.PageEntity
import com.dertefter.data.datasource.local.room.entity.PageType
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.notifications.NotificationDto
import com.jamal_aliev.paginator.cursor.CursorPagingCore.Companion.UNLIMITED_CAPACITY
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import kotlinx.coroutines.flow.Flow
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.cache.eviction.CursorMostRecentPagingCache
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@Inject
class NotificationsRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val crashlyticsRepository: CrashlyticsRepository,
) : NotificationsRepository {

    private val activePaginators = CopyOnWriteArrayList<WeakReference<MutableCursorPaginator<String, NotificationDto>>>()

    override fun getNotificationsPaginator(type: String?): MutableCursorPaginator<String, NotificationDto> {
        val cacheKey = type ?: "all"
        return mutableCursorPaginator(capacity = UNLIMITED_CAPACITY) {
            cache = CursorMostRecentPagingCache(maxSize = 50)
            load { cursor ->
                val result = loadCursorResultWithLocalFallback(
                    localDataSource = localDataSource,
                    crashlyticsRepository = crashlyticsRepository,
                    type = PageType.NOTIFICATION,
                    tab = cacheKey,
                    cursor = cursor,
                    remote = {
                        val startOffset = cursor?.self?.takeIf { it != "initial" }?.toIntOrNull() ?: 0
                        var currentOffset = startOffset
                        val accumulatedNotifications = mutableListOf<NotificationDto>()
                        var nextOffset: String?

                        while (true) {
                            val data = remoteDataSource.getNotifications(currentOffset)
                                .onFailureLog(crashlyticsRepository).getOrThrow()

                            val pageFiltered = if (type == null) {
                                data.notifications
                            } else {
                                data.notifications.filter { it.type.equals(type, ignoreCase = true) }
                            }

                            accumulatedNotifications.addAll(pageFiltered)

                            val fetchedSize = data.notifications.size
                            currentOffset += if (fetchedSize > 0) fetchedSize else 20
                            if (accumulatedNotifications.size >= 20 || !data.hasMore || (currentOffset - startOffset >= 1000)) {
                                nextOffset = if (data.hasMore) currentOffset.toString() else null
                                break
                            }
                        }

                        CursorLoadResult(
                            data = accumulatedNotifications,
                            bookmark = CursorBookmark(
                                prev = cursor?.prev,
                                self = cursor?.self ?: "initial",
                                next = nextOffset
                            )
                        )
                    },
                    local = { localDataSource.getNotificationsForPage(PageType.NOTIFICATION, cacheKey, cursor?.self ?: "initial") }
                )

                localDataSource.upsertPage(
                    PageEntity(
                        type = PageType.NOTIFICATION,
                        tab = cacheKey,
                        self = result.bookmark.self,
                        prev = result.bookmark.prev,
                        next = result.bookmark.next,
                        isEmpty = result.data.isEmpty()
                    )
                )
                localDataSource.saveNotifications(PageType.NOTIFICATION, cacheKey, result.bookmark.self, result.data)

                result
            }
            initialCursor = CursorBookmark(prev = null, self = "initial", next = null)
        }.also {
            activePaginators.add(WeakReference(it))
        }
    }

    override fun getNotificationCount(): Flow<Int?> {
        return localDataSource.notificationCount
    }

    override suspend fun updateNotificationCount(): Result<Unit> {
        return remoteDataSource.getNotificationCount().onFailureLog(crashlyticsRepository).onSuccess {
            localDataSource.saveNotificationCount(it)
        }.map{}
    }

    override suspend fun readAll(): Result<Unit> {
        return remoteDataSource.readAllNotifications().onFailureLog(crashlyticsRepository).onSuccess {
            localDataSource.saveNotificationCount(0)
        }
    }
}
