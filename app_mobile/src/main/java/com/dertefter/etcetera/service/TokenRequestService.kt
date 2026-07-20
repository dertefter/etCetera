package com.dertefter.etcetera.service

import android.content.Intent
import android.util.Log
import com.dertefter.data.datasource.local.TokenManager
import com.dertefter.data.repository.AuthRepository
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TokenRequestService : WearableListenerService() {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var tokenManager: TokenManager

    private val dataClient by lazy { Wearable.getDataClient(this) }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            val login = authRepository.currentLogin.first()
            if (login != null) {
                val access = tokenManager.getAccessTokenForLogin(login).first()
                val refresh = tokenManager.getRefreshTokenForLogin(login).first()
                if (access != null && refresh != null) {
                    syncTokensToWearable(login, access, refresh)
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/request_token_refresh") {
            scope.launch {
                authRepository.refreshToken()
                val login = authRepository.currentLogin.first()
                if (login != null) {
                    val access = tokenManager.getAccessTokenForLogin(login).first()
                    val refresh = tokenManager.getRefreshTokenForLogin(login).first()
                    if (access != null && refresh != null) {
                        syncTokensToWearable(login, access, refresh)
                    }
                }
            }
        }
    }

    private fun syncTokensToWearable(login: String, accessToken: String, refreshToken: String) {
        val putDataMapRequest = PutDataMapRequest.create("/tokens").apply {
            dataMap.putString("login", login)
            dataMap.putString("access_token", accessToken)
            dataMap.putString("refresh_token", refreshToken)
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }

        val putDataRequest = putDataMapRequest.asPutDataRequest()
        putDataRequest.setUrgent()

        dataClient.putDataItem(putDataRequest)
            .addOnSuccessListener { Log.d("TokenRequestService", "Tokens synced to wearable after refresh") }
            .addOnFailureListener { Log.e("TokenRequestService", "Failed to sync tokens to wearable", it) }
    }
}
