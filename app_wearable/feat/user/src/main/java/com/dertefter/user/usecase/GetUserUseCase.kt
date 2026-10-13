package com.dertefter.user.usecase

import com.dertefter.data.dto.user.UserDto
import com.dertefter.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import dev.zacsweers.metro.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String): Flow<UserDto?> {
        return userRepository.getUserById(userId)
    }
}
