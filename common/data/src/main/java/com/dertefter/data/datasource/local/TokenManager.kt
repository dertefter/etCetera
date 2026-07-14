package com.dertefter.data.datasource.local

interface TokenManager {
    fun saveAccessTokenForLogin(login: String, token: String)
    fun getAccessTokenForLogin(login: String): String?
    fun saveRefreshTokenForLogin(login: String, token: String)
    fun getRefreshTokenForLogin(login: String): String?
    fun deleteAccessTokenForLogin(login: String)
    fun deleteRefreshTokenForLogin(login: String)
}
