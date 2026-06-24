package com.dertefter.data.repository

import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.feed.PollDto
import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.dto.feed.stats.PostStatsDto
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeedPaginator(tab: String): MutableCursorPaginator<String, PostDto>

    fun getHashtagPaginator(hashtag: String): MutableCursorPaginator<String, PostDto>

    suspend fun updatePostStats(ids: List<String>): Result<List<PostStatsDto>>

    suspend fun likePost(postId: String): Result<LikeResponseDto>

    suspend fun unlikePost(postId: String): Result<LikeResponseDto>

    suspend fun votePoll(postId: String, optionIds: List<String>): Result<PollDto>

    fun getPost(postId: String): Flow<PostDto>

    suspend fun updatePost(postId: String): Result<PostDto>
}
