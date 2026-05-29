package com.dertefter.data.repository

import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.feed.PollDto
import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.dto.feed.stats.PostStatsDto
import com.jamal_aliev.paginator.MutableCursorPaginator

interface FeedRepository {
    fun getFeedPaginator(tab: String): MutableCursorPaginator<PostDto>

    suspend fun updatePostStats(ids: List<String>): Result<List<PostStatsDto>>

    suspend fun likePost(postId: String): Result<LikeResponseDto>

    suspend fun unlikePost(postId: String): Result<LikeResponseDto>

    suspend fun votePoll(postId: String, optionIds: List<String>): Result<PollDto>
}
