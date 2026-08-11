package com.dertefter.auth.usecase

import com.dertefter.data.dto.auth.SignInResponse
import com.dertefter.data.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        login: String,
        password: String,
        turnstileToken: String,
    ): Result<SignInResponse> {
        return authRepository.signIn(
            login,
            password,
            turnstileToken
        )
    }
}
