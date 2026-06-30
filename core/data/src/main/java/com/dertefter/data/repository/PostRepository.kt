package com.dertefter.data.repository

import com.dertefter.data.dto.feed.PollDto
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.dto.feed.stats.PostStatsDto
import com.dertefter.data.dto.new_post.NewPostRequestDto
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun updatePostStats(ids: List<String>): Result<List<PostStatsDto>>

    suspend fun likePost(postId: String): Result<LikeResponseDto>

    suspend fun deletePost(postId: String): Result<Unit>

    suspend fun unlikePost(postId: String): Result<LikeResponseDto>

    suspend fun pinPost(postId: String): Result<Unit>

    suspend fun unpinPost(postId: String): Result<Unit>

    suspend fun votePoll(postId: String, optionIds: List<String>): Result<PollDto>

    fun getPost(postId: String): Flow<PostDto?>

    suspend fun updatePost(postId: String): Result<PostDto>

    suspend fun newPost(newPostRequestDto: NewPostRequestDto): Result<PostDto>

    suspend fun repost(postId: String, newPostRequestDto: NewPostRequestDto): Result<PostDto>

    fun registerPaginator(paginator: MutableCursorPaginator<String, PostDto>)
}
