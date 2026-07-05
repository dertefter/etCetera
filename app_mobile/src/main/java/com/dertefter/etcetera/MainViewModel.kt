package com.dertefter.etcetera

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.datasource.local.TokenManager
import com.dertefter.data.repository.AuthRepository
import com.dertefter.etcetera.presentation.MainScreenState
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val dataClient by lazy { Wearable.getDataClient(context) }

    val mainScreenState: StateFlow<MainScreenState> = authRepository.isAuthorized
        .map { isAuthorized ->
            MainScreenState(isAuthorized = isAuthorized)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainScreenState(isAuthorized = null)
        )

    init {
        authRepository.isAuthorized.onEach { isAuthorized ->
            if (isAuthorized) {
                syncTokensToWearable()
            }
        }.launchIn(viewModelScope)
    }

    private fun syncTokensToWearable() {
        val accessToken = tokenManager.getAccessToken()
        val refreshToken = tokenManager.getRefreshToken()

        val putDataMapRequest = PutDataMapRequest.create("/tokens").apply {
            accessToken?.let { dataMap.putString("access_token", it) }
            refreshToken?.let { dataMap.putString("refresh_token", it) }
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }

        val putDataRequest = putDataMapRequest.asPutDataRequest()
        putDataRequest.setUrgent()

        dataClient.putDataItem(putDataRequest)
            .addOnSuccessListener { Log.d("MainViewModel", "Tokens synced to wearable") }
            .addOnFailureListener { Log.e("MainViewModel", "Failed to sync tokens to wearable", it) }
    }

}
