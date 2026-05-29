package com.dertefter.data.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val isAuthorized: Flow<Boolean>

    suspend fun signIn(
        email: String,
        password: String,
        turnstileToken: String
    ): Result<Unit>

    suspend fun refreshToken(): Result<Unit>

}