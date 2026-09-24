package com.dertefter.comments.usecase

import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.repository.CommentsRepository
import javax.inject.Inject

class LikeCommentUseCase @Inject constructor(
    private val commentsRepository: CommentsRepository
) {
    suspend operator fun invoke(commentId: String): Result<LikeResponseDto> {
        return commentsRepository.likeComment(commentId)
    }
}
