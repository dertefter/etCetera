package com.dertefter.user.usecase

import com.dertefter.data.repository.PostRepository
import dev.zacsweers.metro.Inject

@Inject
class DeletePostUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String): Result<Unit> {
        return postRepository.deletePost(postId)
    }
}
