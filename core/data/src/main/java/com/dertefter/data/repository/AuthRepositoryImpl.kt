package com.dertefter.data.repository

import com.dertefter.data.datasource.local.TokenManager
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.auth.SignInRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val tokenManager: TokenManager
) : AuthRepository {

    override val isAuthorized: Flow<Boolean> = tokenManager.hasRefreshToken

    override suspend fun signIn(
        email: String,
        password: String,
        turnstileToken: String
    ): Result<Unit> {
        return remoteDataSource.signIn(
            SignInRequest(
                email = email,
                password = password,
                turnstileToken = turnstileToken
            )
        )
    }

    override suspend fun refreshToken(): Result<Unit> {
        return remoteDataSource.refreshToken()
    }

}
