package com.dertefter.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dertefter.data.datasource.local.room.entity.AuthSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthSessionDao {

    @Query("SELECT * FROM auth_sessions")
    fun getAuthSessions(): Flow<List<AuthSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthSessions(sessions: List<AuthSessionEntity>)

    @Query("DELETE FROM auth_sessions")
    suspend fun clearAuthSessions()
}
