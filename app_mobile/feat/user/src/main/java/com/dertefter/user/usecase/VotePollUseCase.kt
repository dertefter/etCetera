package com.dertefter.user.usecase

import com.dertefter.data.dto.feed.PollDto
import com.dertefter.data.repository.PostRepository
import dev.zacsweers.metro.Inject

class VotePollUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String, optionIds: List<String>): Result<PollDto> {
        return postRepository.votePoll(postId, optionIds)
    }
}
