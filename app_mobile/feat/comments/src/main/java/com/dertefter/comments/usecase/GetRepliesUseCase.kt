package com.dertefter.comments.usecase

import com.dertefter.data.dto.comments.RepliesDataDto
import com.dertefter.data.repository.CommentsRepository
import dev.zacsweers.metro.Inject

@Inject
class GetRepliesUseCase(
    private val commentsRepository: CommentsRepository
) {
    suspend operator fun invoke(commentId: String, page: String? = null): Result<RepliesDataDto> {
        return commentsRepository.getReplies(commentId, page)
    }
}
