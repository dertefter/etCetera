package com.dertefter.data.repository

import com.dertefter.data.dto.notifications.NotificationDto
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {
    fun getNotificationsPaginator(type: String? = null): MutableCursorPaginator<String, NotificationDto>

    fun getNotificationCount(): Flow<Int?>

    suspend fun updateNotificationCount(): Result<Unit>

    suspend fun readAll(): Result<Unit>
}
