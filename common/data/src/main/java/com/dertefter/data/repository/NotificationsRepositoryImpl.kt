package com.dertefter.data.repository

import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.NotificationPagingCache
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.notifications.NotificationDto
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.cache.eviction.CursorMostRecentPagingCache
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val crashlyticsRepository: CrashlyticsRepository,
) : NotificationsRepository {

    private val activePaginators = CopyOnWriteArrayList<WeakReference<MutableCursorPaginator<String, NotificationDto>>>()

    override fun getNotificationsPaginator(type: String?): MutableCursorPaginator<String, NotificationDto> {
        return mutableCursorPaginator<String, NotificationDto>(capacity = 20) {
            cache = CursorMostRecentPagingCache(maxSize = 50)
            persistentCache = NotificationPagingCache(type ?: "all", localDataSource)
            load { cursor ->
                val startOffset = cursor?.self?.toIntOrNull() ?: 0
                var currentOffset = startOffset
                val accumulatedNotifications = mutableListOf<NotificationDto>()
                var nextOffset: String?

                while (true) {
                    val result = remoteDataSource.getNotifications(currentOffset).onFailureLog(crashlyticsRepository)
                    val data = result.getOrThrow()

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
                        prev = if (startOffset > 0) (startOffset - 20).coerceAtLeast(0).toString() else null,
                        self = cursor?.self ?: "initial",
                        next = nextOffset
                    )
                )
            }
            initialCursor = CursorBookmark(prev = null, self = "initial", next = null)
        }.also {
            activePaginators.add(WeakReference(it))
        }
    }
}
