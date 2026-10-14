package com.dertefter.user.usecase

import com.dertefter.data.dto.user.BlockResponseDto
import com.dertefter.data.repository.UserRepository
import dev.zacsweers.metro.Inject

@Inject
class BlockUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<BlockResponseDto> {
        return userRepository.block(userId)
    }
}
