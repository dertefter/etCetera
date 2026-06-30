package com.dertefter.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dertefter.data.datasource.local.room.entity.NotificationEntity
import com.dertefter.data.datasource.local.room.entity.PageNotificationEntity
import com.dertefter.data.datasource.local.room.entity.PageType
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNotifications(notifications: List<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPageNotifications(pageNotifications: List<PageNotificationEntity>)

    @Query("""
        SELECT notifications.* FROM notifications
        INNER JOIN page_notifications ON notifications.id = page_notifications.notificationId
        WHERE page_notifications.type = :type AND page_notifications.tab = :tab AND page_notifications.self = :self
        ORDER BY page_notifications.orderIndex ASC
    """)
    suspend fun getNotificationsForPage(type: PageType, tab: String, self: String): List<NotificationEntity>

    @Query("DELETE FROM page_notifications WHERE type = :type AND tab = :tab AND self = :self")
    suspend fun deletePageNotifications(type: PageType, tab: String, self: String)

    @Query("DELETE FROM page_notifications WHERE type = :type AND tab = :tab")
    suspend fun deleteAllPageNotifications(type: PageType, tab: String)

    @Query("SELECT * FROM notifications")
    suspend fun getAllNotifications(): List<NotificationEntity>

    @Query("SELECT * FROM notifications WHERE id = :notificationId")
    suspend fun getNotificationById(notificationId: String): NotificationEntity?

    @Query("SELECT * FROM notifications WHERE id = :notificationId")
    fun getNotification(notificationId: String): Flow<NotificationEntity?>

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: String)

    @Transaction
    suspend fun savePageWithNotifications(
        type: PageType,
        tab: String,
        self: String,
        notifications: List<NotificationEntity>
    ) {
        upsertNotifications(notifications)
        deletePageNotifications(type, tab, self)
        insertPageNotifications(notifications.mapIndexed { index, notification ->
            PageNotificationEntity(type, tab, self, notification.id, index)
        })
    }
}
