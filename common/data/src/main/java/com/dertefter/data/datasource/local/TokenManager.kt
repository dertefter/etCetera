package com.dertefter.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class TokenManager @Inject constructor(
    @param:ApplicationContext val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _hasRefreshToken = MutableStateFlow(prefs.contains("refresh_token"))
    val hasRefreshToken = _hasRefreshToken.asStateFlow()

    fun saveAccessToken(token: String) {
        if (getAccessToken() == token) return
        Log.d("TokenManager", "Saving Access Token")
        prefs.edit { putString("access_token", token) }
    }

    fun getAccessToken(): String? {
        return prefs.getString("access_token", null)
    }

    fun saveRefreshToken(token: String) {
        if (getRefreshToken() == token) return
        Log.d("TokenManager", "Saving Refresh Token")
        prefs.edit { putString("refresh_token", token) }
        _hasRefreshToken.value = true
    }

    fun getRefreshToken(): String? {
        return prefs.getString("refresh_token", null)
    }

    fun deleteAccessToken() {
        if (getAccessToken() == null) return
        prefs.edit { remove("access_token") }
    }

    fun deleteRefreshToken() {
        if (getRefreshToken() == null) return
        prefs.edit { remove("refresh_token") }
        _hasRefreshToken.value = false
    }

}
