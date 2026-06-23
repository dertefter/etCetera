package com.dertefter.data.repository

import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.comments.NewCommentRequestDto
import com.dertefter.data.dto.comments.RepliesDataDto
import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import com.jamal_aliev.paginator.MutableCursorPaginator
import java.io.File

interface CommentsRepository {

    fun getCommentsPaginator(postId: String, sort: String): MutableCursorPaginator<CommentDto>

    suspend fun likeComment(commentId: String): Result<LikeResponseDto>

    suspend fun unlikeComment(commentId: String): Result<LikeResponseDto>

    suspend fun getReplies(commentId: String, page: String?): Result<RepliesDataDto>

    suspend fun newComment(postId: String, newCommentRequestDto: NewCommentRequestDto): Result<CommentDto>

    suspend fun upload(file: File): Result<AttachmentUploadResponseDto>

}
