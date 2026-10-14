package com.dertefter.comments.usecase

import com.dertefter.data.repository.CommentsRepository
import dev.zacsweers.metro.Inject

@Inject
class DeleteCommentUseCase(
    private val commentsRepository: CommentsRepository
) {
    suspend operator fun invoke(commentId: String): Result<Unit> {
        return commentsRepository.deleteComment(commentId)
    }
}
