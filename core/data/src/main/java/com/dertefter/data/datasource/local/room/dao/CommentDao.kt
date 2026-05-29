package com.dertefter.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dertefter.data.datasource.local.room.entity.CommentEntity
import com.dertefter.data.datasource.local.room.entity.PageCommentEntity
import com.dertefter.data.datasource.local.room.entity.PageType

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertComments(comments: List<CommentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPageComments(pageComments: List<PageCommentEntity>)

    @Query("""
        SELECT comments.* FROM comments
        INNER JOIN page_comments ON comments.id = page_comments.commentId
        WHERE page_comments.type = :type AND page_comments.tab = :tab AND page_comments.self = :self
        ORDER BY page_comments.orderIndex ASC
    """)
    suspend fun getCommentsForPage(type: PageType, tab: String, self: String): List<CommentEntity>

    @Query("SELECT * FROM comments WHERE parentId = :parentId")
    suspend fun getRepliesForComment(parentId: String): List<CommentEntity>

    @Query("SELECT * FROM comments")
    suspend fun getAllComments(): List<CommentEntity>

    @Query("DELETE FROM page_comments WHERE type = :type AND tab = :tab AND self = :self")
    suspend fun deletePageComments(type: PageType, tab: String, self: String)

    @Transaction
    suspend fun savePageWithComments(
        type: PageType,
        tab: String,
        self: String,
        comments: List<CommentEntity>
    ) {
        upsertComments(comments)
        deletePageComments(type, tab, self)
        insertPageComments(comments.mapIndexed { index, comment ->
            PageCommentEntity(type, tab, self, comment.id, index)
        })
    }
}
