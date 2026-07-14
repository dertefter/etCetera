package com.dertefter.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : TokenManager {

    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun saveAccessTokenForLogin(login: String, token: String) {
        prefs.edit { putString("access_token_$login", token) }
    }

    override fun getAccessTokenForLogin(login: String): String? {
        return prefs.getString("access_token_$login", null)
    }

    override fun saveRefreshTokenForLogin(login: String, token: String) {
        prefs.edit { putString("refresh_token_$login", token) }
    }

    override fun getRefreshTokenForLogin(login: String): String? {
        return prefs.getString("refresh_token_$login", null)
    }

    override fun deleteAccessTokenForLogin(login: String) {
        prefs.edit { remove("access_token_$login") }
    }

    override fun deleteRefreshTokenForLogin(login: String) {
        prefs.edit { remove("refresh_token_$login") }
    }
}
