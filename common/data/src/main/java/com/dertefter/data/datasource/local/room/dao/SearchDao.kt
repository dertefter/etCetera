package com.dertefter.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dertefter.data.datasource.local.room.entity.SearchHashtagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {

    @Query("SELECT * FROM trending_hashtags")
    fun getTrendingHashtags(): Flow<List<SearchHashtagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHashtags(hashtags: List<SearchHashtagEntity>)

    @Query("DELETE FROM trending_hashtags")
    suspend fun clearTrendingHashtags()

    @Transaction
    suspend fun updateTrendingHashtags(hashtags: List<SearchHashtagEntity>) {
        clearTrendingHashtags()
        insertHashtags(hashtags)
    }
}
