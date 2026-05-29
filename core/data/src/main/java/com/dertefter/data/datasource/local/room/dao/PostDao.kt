package com.dertefter.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dertefter.data.datasource.local.room.entity.PagePostEntity
import com.dertefter.data.datasource.local.room.entity.PageType
import com.dertefter.data.datasource.local.room.entity.PostEntity

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPosts(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPagePosts(pagePosts: List<PagePostEntity>)

    @Query("""
        SELECT posts.* FROM posts
        INNER JOIN page_posts ON posts.id = page_posts.postId
        WHERE page_posts.type = :type AND page_posts.tab = :tab AND page_posts.self = :self
        ORDER BY page_posts.orderIndex ASC
    """)
    suspend fun getPostsForPage(type: PageType, tab: String, self: String): List<PostEntity>

    @Query("DELETE FROM page_posts WHERE type = :type AND tab = :tab AND self = :self")
    suspend fun deletePagePosts(type: PageType, tab: String, self: String)

    @Query("DELETE FROM page_posts WHERE type = :type AND tab = :tab")
    suspend fun deleteAllPagePosts(type: PageType, tab: String)

    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): List<PostEntity>

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostEntity?

    @Transaction
    suspend fun savePageWithPosts(
        type: PageType,
        tab: String,
        self: String,
        posts: List<PostEntity>
    ) {
        upsertPosts(posts)
        deletePagePosts(type, tab, self)
        insertPagePosts(posts.mapIndexed { index, post ->
            PagePostEntity(type, tab, self, post.id, index)
        })
    }
}
