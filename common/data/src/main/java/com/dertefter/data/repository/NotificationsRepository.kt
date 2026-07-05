package com.dertefter.data.repository

import com.dertefter.data.dto.notifications.NotificationDto
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator

interface NotificationsRepository {
    fun getNotificationsPaginator(type: String? = null): MutableCursorPaginator<String, NotificationDto>
}
