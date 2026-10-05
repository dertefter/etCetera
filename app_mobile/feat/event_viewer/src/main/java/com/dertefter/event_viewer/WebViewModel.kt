package com.dertefter.event_viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.datasource.local.TokenManager
import com.dertefter.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WebViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    val accessToken: StateFlow<String?> = authRepository.currentLogin.flatMapLatest { login ->
        if (login != null) {
            tokenManager.getAccessTokenForLogin(login)
        } else {
            flowOf(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val refreshToken: StateFlow<String?> = authRepository.currentLogin.flatMapLatest { login ->
        if (login != null) {
            tokenManager.getRefreshTokenForLogin(login)
        } else {
            flowOf(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )
}
