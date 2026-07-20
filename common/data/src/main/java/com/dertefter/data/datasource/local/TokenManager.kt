package com.dertefter.data.datasource.local

import kotlinx.coroutines.flow.Flow

interface TokenManager {
    fun saveAccessTokenForLogin(login: String, token: String)
    fun getAccessTokenForLogin(login: String): Flow<String?>
    fun saveRefreshTokenForLogin(login: String, token: String)
    fun getRefreshTokenForLogin(login: String): Flow<String?>
    fun deleteAccessTokenForLogin(login: String)
    fun deleteRefreshTokenForLogin(login: String)
}
