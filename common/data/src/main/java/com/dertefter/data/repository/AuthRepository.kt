package com.dertefter.data.repository

import com.dertefter.data.dto.auth.AuthSessionDto
import com.dertefter.data.dto.auth.SignInResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val isAuthorized: Flow<Boolean?>

    val loginHistory: Flow<List<String>>

    val currentLogin: Flow<String?>

    suspend fun switchToLogin(login: String?)

    suspend fun removeLoginFromHistory(login: String)

    suspend fun signIn(
        email: String,
        password: String,
        turnstileToken: String
    ): Result<SignInResponse>

    suspend fun refreshToken(): Result<Unit>

    fun getAuthSessions(): Flow<List<AuthSessionDto>?>

    suspend fun updateAuthSessions(): Result<Unit>

}