package com.dertefter.comments.usecase

import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.repository.CommentsRepository
import dev.zacsweers.metro.Inject

@Inject
class UnlikeCommentUseCase(
    private val commentsRepository: CommentsRepository
) {
    suspend operator fun invoke(commentId: String): Result<LikeResponseDto> {
        return commentsRepository.unlikeComment(commentId)
    }
}
