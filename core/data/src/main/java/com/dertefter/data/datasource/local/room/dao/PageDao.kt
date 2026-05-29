package com.dertefter.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dertefter.data.datasource.local.room.entity.PageEntity
import com.dertefter.data.datasource.local.room.entity.PageType

@Dao
interface PageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(page: PageEntity)

    @Query("SELECT * FROM pages WHERE type = :type AND tab = :tab AND self = :self")
    suspend fun getPage(type: PageType, tab: String, self: String): PageEntity?

    @Query("SELECT * FROM pages WHERE type = :type AND tab = :tab")
    suspend fun getAllPages(type: PageType, tab: String): List<PageEntity>

    @Query("SELECT * FROM pages WHERE type = :type")
    suspend fun getAllPages(type: PageType): List<PageEntity>

    @Query("DELETE FROM pages WHERE type = :type AND tab = :tab AND self = :self")
    suspend fun deletePage(type: PageType, tab: String, self: String)

    @Query("DELETE FROM pages WHERE type = :type AND tab = :tab")
    suspend fun deleteAll(type: PageType, tab: String)
}
