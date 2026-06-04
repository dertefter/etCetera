package com.dertefter.data.repository

import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.notifications.NotificationDto
import com.jamal_aliev.paginator.CursorPagingCore
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cache.eviction.CursorMostRecentPagingCache
import com.jamal_aliev.paginator.load.CursorLoadResult
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : NotificationsRepository {

    private val activePaginators = CopyOnWriteArrayList<WeakReference<MutableCursorPaginator<NotificationDto>>>()

    override fun getNotificationsPaginator(type: String?): MutableCursorPaginator<NotificationDto> {
        val pagingCore = CursorPagingCore<NotificationDto>(
            cache = CursorMostRecentPagingCache(maxSize = 50)
        )
        return MutableCursorPaginator(
            core = pagingCore,
            load = { cursor ->
                val startOffset = (cursor?.self as? String)?.toIntOrNull() ?: 0
                var currentOffset = startOffset
                val accumulatedNotifications = mutableListOf<NotificationDto>()
                var nextOffset: String? = null

                while (true) {
                    val result = remoteDataSource.getNotifications(currentOffset)
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
                        self = startOffset.toString(),
                        next = nextOffset
                    )
                )
            }
        ).also {
            activePaginators.add(WeakReference(it))
        }
    }
}
