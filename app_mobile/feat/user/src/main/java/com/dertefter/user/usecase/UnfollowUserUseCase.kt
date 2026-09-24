package com.dertefter.user.usecase

import com.dertefter.data.dto.user.FollowResponseDto
import com.dertefter.data.repository.UserRepository
import javax.inject.Inject

class UnfollowUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<FollowResponseDto> {
        return userRepository.unfollow(userId)
    }
}
