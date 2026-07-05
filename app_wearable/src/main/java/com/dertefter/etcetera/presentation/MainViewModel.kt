package com.dertefter.etcetera.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.datasource.local.TokenManager
import com.dertefter.data.repository.AuthRepository
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    @param:ApplicationContext private val context: Context
) : ViewModel(), DataClient.OnDataChangedListener {

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
        dataClient.addListener(this)
        Log.d("MainViewModel", "Initial tokens: Access: ${tokenManager.getAccessToken()}, Refresh: ${tokenManager.getRefreshToken()}")
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/tokens") {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val accessToken = dataMap.getString("access_token")
                val refreshToken = dataMap.getString("refresh_token")

                val localAccessToken = tokenManager.getAccessToken()
                val localRefreshToken = tokenManager.getRefreshToken()

                Log.d("MainViewModel", "Data changed event. Received Access: $accessToken, Refresh: $refreshToken. Local Access: $localAccessToken, Local Refresh: $localRefreshToken")

                if (accessToken != localAccessToken || refreshToken != localRefreshToken) {
                    Log.d("MainViewModel", "Updating local tokens...")
                    if (accessToken != null) {
                        tokenManager.saveAccessToken(accessToken)
                    } else {
                        tokenManager.deleteAccessToken()
                    }
                    if (refreshToken != null) {
                        tokenManager.saveRefreshToken(refreshToken)
                    } else {
                        tokenManager.deleteRefreshToken()
                    }
                }
            }
        }
    }

    override fun onCleared() {
        dataClient.removeListener(this)
    }
}
