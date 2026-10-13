package com.dertefter.data.di

import android.content.Context
import androidx.room.Room
import com.dertefter.data.datasource.local.room.AppDatabase
import com.dertefter.data.datasource.local.room.dao.CommentDao
import com.dertefter.data.datasource.local.room.dao.NotificationDao
import com.dertefter.data.datasource.local.room.dao.PageDao
import com.dertefter.data.datasource.local.room.dao.PostDao
import com.dertefter.data.datasource.local.room.dao.SearchDao
import com.dertefter.data.datasource.local.room.dao.UserDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
object DatabaseModule {

    @Provides
    @SingleIn(AppScope::class)
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "etcetera-database",
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun providePageDao(database: AppDatabase): PageDao {
        return database.pageDao()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun providePostDao(database: AppDatabase): PostDao {
        return database.postDao()
    }

    @Provides
    fun provideCommentDao(database: AppDatabase): CommentDao {
        return database.commentDao()
    }

    @Provides
    fun provideSearchDao(database: AppDatabase): SearchDao {
        return database.searchDao()
    }

    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDao {
        return database.notificationDao()
    }

}
