package com.dertefter.data.repository

import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.comments.RepliesDataDto
import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.jamal_aliev.paginator.MutableCursorPaginator

interface CommentsRepository {

    fun getCommentsPaginator(postId: String, sort: String): MutableCursorPaginator<CommentDto>

    suspend fun likeComment(commentId: String): Result<LikeResponseDto>

    suspend fun unlikeComment(commentId: String): Result<LikeResponseDto>

    suspend fun getReplies(commentId: String, page: String?): Result<RepliesDataDto>

}
