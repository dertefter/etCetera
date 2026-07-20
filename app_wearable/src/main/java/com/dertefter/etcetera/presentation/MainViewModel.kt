package com.dertefter.etcetera.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.datasource.local.TokenManager
import com.dertefter.data.repository.AuthRepository
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
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
        requestTokensFromPhone()
    }

    private fun requestTokensFromPhone() {
        viewModelScope.launch {
            try {
                val nodes = Tasks.await(Wearable.getNodeClient(context).connectedNodes)
                val messageClient = Wearable.getMessageClient(context)
                for (node in nodes) {
                    messageClient.sendMessage(node.id, "/request_token_refresh", byteArrayOf())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun spoilAccessToken() {
        viewModelScope.launch {
            val login = authRepository.currentLogin.first()
            if (login != null) {
                tokenManager.saveAccessTokenForLogin(login, "spoiled_token")
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/tokens") {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val login = dataMap.getString("login")
                val accessToken = dataMap.getString("access_token")
                val refreshToken = dataMap.getString("refresh_token")

                if (login != null && accessToken != null && refreshToken != null) {
                    tokenManager.saveAccessTokenForLogin(login, accessToken)
                    tokenManager.saveRefreshTokenForLogin(login, refreshToken)
                    viewModelScope.launch {
                        authRepository.switchToLogin(login)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        dataClient.removeListener(this)
    }
}
