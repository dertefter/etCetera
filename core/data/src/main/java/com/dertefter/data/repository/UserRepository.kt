package com.dertefter.data.repository

import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.dto.feed.stats.PostStatsDto
import com.dertefter.data.dto.user.FollowResponseDto
import com.dertefter.data.dto.user.UserDto
import com.jamal_aliev.paginator.MutableCursorPaginator
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUser(userId: String): Flow<UserDto?>

    suspend fun updateUser(userId: String): Result<UserDto>

    fun getPostsPaginator(userId: String, sort: String = "new"): MutableCursorPaginator<PostDto>

    fun getLikedPostsPaginator(userId: String): MutableCursorPaginator<PostDto>

    suspend fun updatePostStats(ids: List<String>): Result<List<PostStatsDto>>

    suspend fun likePost(postId: String): Result<LikeResponseDto>

    suspend fun unlikePost(postId: String): Result<LikeResponseDto>

    suspend fun follow(userId: String): Result<FollowResponseDto>

    suspend fun unfollow(userId: String): Result<FollowResponseDto>
}
