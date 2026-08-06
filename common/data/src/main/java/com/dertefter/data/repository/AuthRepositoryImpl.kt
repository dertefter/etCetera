package com.dertefter.data.repository

import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.auth.SignInRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val crashlyticsRepository: CrashlyticsRepository,
) : AuthRepository {

    override val isAuthorized: Flow<Boolean?> = localDataSource.currentLogin.map { it != null }

    override val loginHistory: Flow<List<String>> = localDataSource.loginHistory

    override val currentLogin: Flow<String?> = localDataSource.currentLogin

    override suspend fun switchToLogin(login: String?) {
        localDataSource.switchToLogin(login)
    }

    override suspend fun removeLoginFromHistory(login: String) {
        localDataSource.removeLoginFromHistory(login)
    }

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
        ).onFailureLog(crashlyticsRepository)
    }

    override suspend fun refreshToken(): Result<Unit> {
        return remoteDataSource.refreshToken().onFailureLog(crashlyticsRepository)
    }

}
