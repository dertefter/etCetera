package com.dertefter.event_viewer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.datasource.local.TokenManager
import com.dertefter.data.dto.event.CurrentEvent
import com.dertefter.data.repository.AuthRepository
import com.dertefter.data.repository.EventsRepository
import com.dertefter.navigation.DeepLinkParser
import com.dertefter.navigation.Navigator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalCoroutinesApi::class)
@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class WebViewModel(
    private val authRepository: AuthRepository,
    eventsRepository: EventsRepository,
    private val tokenManager: TokenManager,
    private val navigator: Navigator,
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

    val currentEvent: Flow<CurrentEvent?> = eventsRepository.currentEvent

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

    suspend fun refreshAccessToken() {
        authRepository.refreshToken()
            .onFailure { Log.w(TAG, "Не удалось обновить токен для ивента", it) }
    }

    fun closeEvent() {
        navigator.navigateUp()
    }


    fun openSocial(path: String) {
        val route = DeepLinkParser.parsePath(path)
        navigator.navigateUp()
        route?.let(navigator::navigate)
    }

    private companion object {
        const val TAG = "WebRoute"
    }
}
