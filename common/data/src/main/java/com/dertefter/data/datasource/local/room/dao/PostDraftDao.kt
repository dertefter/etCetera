package com.dertefter.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dertefter.data.datasource.local.room.entity.PostDraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDraftDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePostDraft(draft: PostDraftEntity)

    @Query("SELECT * FROM post_drafts WHERE draftKey = :draftKey")
    fun getPostDraft(draftKey: String): Flow<PostDraftEntity?>

    @Query("DELETE FROM post_drafts WHERE draftKey = :draftKey")
    suspend fun deletePostDraft(draftKey: String)
}
