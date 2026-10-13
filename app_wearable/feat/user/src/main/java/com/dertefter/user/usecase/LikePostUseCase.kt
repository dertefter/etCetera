package com.dertefter.user.usecase

import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.repository.PostRepository
import dev.zacsweers.metro.Inject

class LikePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String): Result<LikeResponseDto> {
        return postRepository.likePost(postId)
    }
}
