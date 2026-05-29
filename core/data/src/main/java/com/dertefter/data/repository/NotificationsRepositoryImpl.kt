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

    override fun getNotificationsPaginator(): MutableCursorPaginator<NotificationDto> {
        val pagingCore = CursorPagingCore<NotificationDto>(
            cache = CursorMostRecentPagingCache(maxSize = 20)
        )
        return MutableCursorPaginator(
            core = pagingCore,
            load = { cursor ->
                val offset = (cursor?.self as? String)?.toIntOrNull() ?: 0
                val result = remoteDataSource.getNotifications(offset)
                val data = result.getOrThrow()

                CursorLoadResult(
                    data = data.notifications,
                    bookmark = CursorBookmark(
                        prev = if (offset > 0) (offset - 20).coerceAtLeast(0).toString() else null,
                        self = offset.toString(),
                        next = if (data.hasMore) (offset + data.notifications.size).toString() else null
                    )
                )
            }
        ).also {
            activePaginators.add(WeakReference(it))
        }
    }
}
