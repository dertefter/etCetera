package com.dertefter.user.usecase

import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.repository.PostRepository
import dev.zacsweers.metro.Inject

@Inject
class UnlikePostUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String): Result<LikeResponseDto> {
        return postRepository.unlikePost(postId)
    }
}
