package com.dertefter.user.usecase

import com.dertefter.data.dto.user.UserDto
import com.dertefter.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import dev.zacsweers.metro.Inject

@Inject
class GetUserByIdUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String): Flow<UserDto?> {
        return userRepository.getUserById(userId)
    }
}

@Inject
class GetUserByUsernameUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(username: String): Flow<UserDto?> {
        return userRepository.getUserByUsername(username)
    }
}
