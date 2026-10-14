package com.dertefter.user.usecase

import com.dertefter.data.dto.user.FollowResponseDto
import com.dertefter.data.repository.UserRepository
import dev.zacsweers.metro.Inject

@Inject
class UnfollowUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<FollowResponseDto> {
        return userRepository.unfollow(userId)
    }
}
