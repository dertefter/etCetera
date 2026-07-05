package com.dertefter.data.repository

import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.comments.NewCommentRequestDto
import com.dertefter.data.dto.comments.RepliesDataDto
import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator

interface CommentsRepository {

    fun getCommentsPaginator(postId: String, sort: String): MutableCursorPaginator<String, CommentDto>

    suspend fun likeComment(commentId: String): Result<LikeResponseDto>

    suspend fun unlikeComment(commentId: String): Result<LikeResponseDto>

    suspend fun getReplies(commentId: String, page: String?): Result<RepliesDataDto>

    suspend fun deleteComment(commentId: String): Result<Unit>

    suspend fun newComment(postId: String, newCommentRequestDto: NewCommentRequestDto): Result<CommentDto>

    suspend fun newCommentReply(
        commentId: String,
        newCommentRequestDto: NewCommentRequestDto
    ): Result<CommentDto>

}
