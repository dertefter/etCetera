package com.dertefter.user.usecase

import com.dertefter.data.dto.user.BlockResponseDto
import com.dertefter.data.repository.UserRepository
import javax.inject.Inject

class UnblockUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<BlockResponseDto> {
        return userRepository.unblock(userId)
    }
}
