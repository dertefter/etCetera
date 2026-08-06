package com.dertefter.etcetera

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.datasource.local.TokenManager
import com.dertefter.data.repository.AuthRepository
import com.dertefter.data.repository.CrashlyticsRepository
import com.dertefter.data.repository.MeRepository
import com.dertefter.etcetera.service.TokenRequestService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MainUiState(
    val isReady: Boolean = false,
    val currentLogin: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    authRepository: AuthRepository,
    meRepository: MeRepository,
    crashlyticsRepository: CrashlyticsRepository,
    private val tokenManager: TokenManager,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = authRepository.currentLogin.map { login ->
        MainUiState(isReady = true, currentLogin = login)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = MainUiState(isReady = false)
    )

    val currentError = crashlyticsRepository.currentError.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = null
    )

    val meUserId: StateFlow<String?> = meRepository.meDto.map { it?.id }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = null
    )

    val currentLogin: StateFlow<String?> = uiState.map { it.currentLogin }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = null
    )

    val refreshToken: Flow<String?> = currentLogin.flatMapLatest { login ->
        if (login != null) {
            tokenManager.getRefreshTokenForLogin(login)
        } else {
            flowOf(null)
        }
    }

    val accessToken: Flow<String?> = currentLogin.flatMapLatest { login ->
        if (login != null) {
            tokenManager.getAccessTokenForLogin(login)
        } else {
            flowOf(null)
        }
    }

    init {
        combine(currentLogin, accessToken, refreshToken) { _, _, _ -> }.onEach {
            context.startService(Intent(context, TokenRequestService::class.java))
            meRepository.fetchMe()
        }.launchIn(viewModelScope)
    }

}
