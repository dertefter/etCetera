package com.dertefter.comments.usecase

import com.dertefter.data.repository.CommentsRepository
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val commentsRepository: CommentsRepository
) {
    suspend operator fun invoke(commentId: String): Result<Unit> {
        return commentsRepository.deleteComment(commentId)
    }
}
