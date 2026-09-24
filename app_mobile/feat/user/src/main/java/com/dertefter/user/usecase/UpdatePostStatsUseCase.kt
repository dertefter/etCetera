package com.dertefter.user.usecase

import com.dertefter.data.dto.feed.stats.PostStatsDto
import com.dertefter.data.repository.PostRepository
import javax.inject.Inject

class UpdatePostStatsUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(ids: List<String>): Result<List<PostStatsDto>> {
        return postRepository.updatePostStats(ids)
    }
}
