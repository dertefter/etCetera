package com.dertefter.data.repository

import com.dertefter.data.dto.notifications.NotificationDto
import com.jamal_aliev.paginator.MutableCursorPaginator

interface NotificationsRepository {
    fun getNotificationsPaginator(type: String? = null): MutableCursorPaginator<NotificationDto>

}
