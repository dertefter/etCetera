package com.dertefter.user.usecase

import com.dertefter.data.dto.user.UserDto
import com.dertefter.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String): Flow<UserDto?> {
        return userRepository.getUserById(userId)
    }
}

class GetUserByUsernameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(username: String): Flow<UserDto?> {
        return userRepository.getUserByUsername(username)
    }
}
