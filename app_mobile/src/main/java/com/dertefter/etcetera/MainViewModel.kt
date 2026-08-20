package com.dertefter.etcetera

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.datasource.local.TokenManager
import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor
import com.dertefter.data.repository.AuthRepository
import com.dertefter.data.repository.CrashlyticsRepository
import com.dertefter.data.repository.MeRepository
import com.dertefter.data.repository.SettingsRepository
import com.dertefter.etcetera.presentation.MainUiState
import com.dertefter.etcetera.presentation.ThemeState
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



@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    authRepository: AuthRepository,
    meRepository: MeRepository,
    crashlyticsRepository: CrashlyticsRepository,
    settingsRepository: SettingsRepository,
    private val tokenManager: TokenManager,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = combine(
        authRepository.currentLogin,
        meRepository.meDto.map { it?.id },
        crashlyticsRepository.currentError
    ) { login, meId, error ->
        MainUiState(
            isReady = true,
            currentLogin = login,
            meUserId = meId,
            currentError = error
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = MainUiState(isReady = false)
    )

    val themeState: StateFlow<ThemeState?> = combine(
        settingsRepository.emojiAvatarHarmonizationColor,
        settingsRepository.darkTheme
    ) { emojiAvatarHarmonizationColor, darkTheme ->
        ThemeState(
            emojiAvatarHarmonizationColor,
            darkTheme
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = null
    )

    val isReady: StateFlow<Boolean> = combine(
        themeState,
        uiState,
    ) { themeState, uiState ->
        themeState != null && uiState.isReady
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = false
    )


    val currentLogin: Flow<String?> = uiState.map { it.currentLogin }

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
