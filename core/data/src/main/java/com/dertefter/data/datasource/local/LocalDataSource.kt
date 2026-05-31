package com.dertefter.data.datasource.local

import com.dertefter.data.datasource.local.room.entity.PageEntity
import com.dertefter.data.datasource.local.room.entity.PageType
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.data.dto.me.MeDto
import com.dertefter.data.dto.search.SearchHashtagDto
import com.dertefter.data.dto.user.UserDto
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    val meDto: Flow<MeDto?>
    suspend fun saveMe(meDto: MeDto)

    fun getUser(userId: String): Flow<UserDto?>
    suspend fun saveUser(userDto: UserDto)

    suspend fun upsertPage(page: PageEntity)
    suspend fun getPage(type: PageType, tab: String, self: String): PageEntity?
    suspend fun getAllPages(type: PageType, tab: String): List<PageEntity>
    suspend fun getAllPages(type: PageType): List<PageEntity>
    suspend fun deletePage(type: PageType, tab: String, self: String)
    suspend fun deleteAllPages(type: PageType, tab: String)

    suspend fun savePosts(type: PageType, tab: String, self: String, posts: List<PostDto>)
    suspend fun getPostsForPage(type: PageType, tab: String, self: String): List<PostDto>

    suspend fun getAllPosts(): List<PostDto>
    suspend fun savePost(post: PostDto)

    suspend fun saveComments(type: PageType, tab: String, self: String, comments: List<CommentDto>)
    suspend fun getCommentsForPage(type: PageType, tab: String, self: String): List<CommentDto>
    suspend fun getAllComments(): List<CommentDto>
    suspend fun saveComment(comment: CommentDto)

    suspend fun saveUsers(type: PageType, tab: String, self: String, users: List<FollowerUserDto>)
    suspend fun getUsersForPage(type: PageType, tab: String, self: String): List<FollowerUserDto>

    fun getTrendingHashtags(): Flow<List<SearchHashtagDto>?>
    suspend fun saveTrendingHashtags(hashtags: List<SearchHashtagDto>)
}
