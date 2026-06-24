package com.dertefter.data.datasource.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dertefter.data.datasource.local.room.converters.RoomConverters
import com.dertefter.data.datasource.local.room.dao.CommentDao
import com.dertefter.data.datasource.local.room.dao.PageDao
import com.dertefter.data.datasource.local.room.dao.PostDao
import com.dertefter.data.datasource.local.room.dao.SearchDao
import com.dertefter.data.datasource.local.room.dao.UserDao
import com.dertefter.data.datasource.local.room.entity.CommentEntity
import com.dertefter.data.datasource.local.room.entity.PageCommentEntity
import com.dertefter.data.datasource.local.room.entity.PageEntity
import com.dertefter.data.datasource.local.room.entity.PagePostEntity
import com.dertefter.data.datasource.local.room.entity.PageUserEntity
import com.dertefter.data.datasource.local.room.entity.PostEntity
import com.dertefter.data.datasource.local.room.entity.SearchHashtagEntity
import com.dertefter.data.datasource.local.room.entity.UserEntity

@Database(
    entities = [
        PageEntity::class,
        UserEntity::class,
        PostEntity::class,
        PagePostEntity::class,
        CommentEntity::class,
        PageCommentEntity::class,
        PageUserEntity::class,
        SearchHashtagEntity::class
    ],
    version = 44,
    exportSchema = false
)


@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pageDao(): PageDao
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun searchDao(): SearchDao

}
