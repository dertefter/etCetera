package com.dertefter.comments.usecase

import com.dertefter.data.dto.comments.RepliesDataDto
import com.dertefter.data.repository.CommentsRepository
import javax.inject.Inject

class GetRepliesUseCase @Inject constructor(
    private val commentsRepository: CommentsRepository
) {
    suspend operator fun invoke(commentId: String, page: String? = null): Result<RepliesDataDto> {
        return commentsRepository.getReplies(commentId, page)
    }
}
