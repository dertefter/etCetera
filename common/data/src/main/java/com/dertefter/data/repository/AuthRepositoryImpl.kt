package com.dertefter.data.repository

import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.auth.AuthSessionDto
import com.dertefter.data.dto.auth.SignInRequest
import com.dertefter.data.dto.auth.SignInResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
    ): Result<SignInResponse> {
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

    override fun getAuthSessions(): Flow<List<AuthSessionDto>?> = localDataSource.authSessions

    override suspend fun updateAuthSessions(): Result<Unit> {
        return remoteDataSource.getAuthSessions().onFailureLog(crashlyticsRepository).onSuccess { response ->
            localDataSource.saveAuthSessions(response.sessions)
        }.map { Unit }
    }

    override suspend fun deleteAuthSession(sessionId: String): Result<Unit> {
        return remoteDataSource.deleteAuthSession(sessionId).onFailureLog(crashlyticsRepository).onSuccess {
            localDataSource.deleteAuthSession(sessionId)
        }
    }

    override suspend fun deleteAllSessions(): Result<Unit> {
        return remoteDataSource.deleteAllAuthSessions().onFailureLog(crashlyticsRepository).onSuccess {
            updateAuthSessions()
        }
    }

}
