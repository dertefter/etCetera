package com.dertefter.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val hasRefreshToken: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[REFRESH_TOKEN] != null
        }
        .distinctUntilChanged()

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { it[ACCESS_TOKEN] = token }
    }

    suspend fun getAccessToken(): String? {
        return dataStore.data.first()[ACCESS_TOKEN]
    }

    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { it[REFRESH_TOKEN] = token }
    }

    suspend fun getRefreshToken(): String? {
        return dataStore.data.first()[REFRESH_TOKEN]
    }

    suspend fun deleteAccessToken() {
        dataStore.edit { it.remove(ACCESS_TOKEN) }
    }

    suspend fun deleteRefreshToken() {
        dataStore.edit { it.remove(REFRESH_TOKEN) }
    }
}
