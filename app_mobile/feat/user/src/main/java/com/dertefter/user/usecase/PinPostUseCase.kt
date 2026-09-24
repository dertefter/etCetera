package com.dertefter.user.usecase

import com.dertefter.data.repository.PostRepository
import javax.inject.Inject

class PinPostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String): Result<Unit> {
        return postRepository.pinPost(postId)
    }
}
