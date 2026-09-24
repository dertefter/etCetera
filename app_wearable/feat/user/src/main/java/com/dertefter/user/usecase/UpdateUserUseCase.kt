package com.dertefter.user.usecase

import com.dertefter.data.dto.user.UserDto
import com.dertefter.data.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<UserDto> {
        return userRepository.updateUser(userId)
    }
}
