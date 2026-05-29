package com.dertefter.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dertefter.data.datasource.local.room.entity.PageType
import com.dertefter.data.datasource.local.room.entity.PageUserEntity
import com.dertefter.data.datasource.local.room.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUser(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE isMe = 1 LIMIT 1")
    fun getMe(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isMe = 1 LIMIT 1")
    suspend fun getMeSync(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPageUsers(pageUsers: List<PageUserEntity>)

    @Query("""
        SELECT users.* FROM users
        INNER JOIN page_users ON users.id = page_users.userId
        WHERE page_users.type = :type AND page_users.tab = :tab AND page_users.self = :self
        ORDER BY page_users.orderIndex ASC
    """)
    suspend fun getUsersForPage(type: PageType, tab: String, self: String): List<UserEntity>

    @Query("DELETE FROM page_users WHERE type = :type AND tab = :tab AND self = :self")
    suspend fun deletePageUsers(type: PageType, tab: String, self: String)

    @Transaction
    suspend fun savePageWithUsers(
        type: PageType,
        tab: String,
        self: String,
        users: List<UserEntity>
    ) {
        insertUsers(users)
        deletePageUsers(type, tab, self)
        insertPageUsers(users.mapIndexed { index, user ->
            PageUserEntity(type, tab, self, user.id, index)
        })
    }
}
