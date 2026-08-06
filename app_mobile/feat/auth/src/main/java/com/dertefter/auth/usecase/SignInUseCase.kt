package com.dertefter.auth.usecase

import com.dertefter.data.repository.AuthRepository
import com.dertefter.data.repository.CrashlyticsRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        login: String,
        password: String,
        turnstileToken: String,
    ): Result<Unit> {
        return authRepository.signIn(
            login,
            password,
            turnstileToken
        )
    }
}
