package com.dertefter.settings_security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import com.dertefter.data.repository.AuthRepository
import com.dertefter.navigation.Navigator
import com.dertefter.settings_security.presentation.Event
import com.dertefter.settings_security.presentation.UiState
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class SettingsSecurityViewModel(
    private val navigator: Navigator,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<UiState> = combine(
        authRepository.getAuthSessions(),
        isRefreshing
    ) { sessions, refreshing ->
        UiState(
            isLoading = refreshing,
            sessions = sessions
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState(isLoading = true, sessions = null)
    )

    init {
        refresh()
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.OnNavigateBack -> navigator.navigateUp()
            Event.OnRefresh -> refresh()
            is Event.OnDeleteSession -> deleteSession(event.sessionId)
            Event.OnDeleteAllSessions -> {
                viewModelScope.launch {
                    authRepository.deleteAllSessions()
                }
            }
        }
    }

    private fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            authRepository.deleteAuthSession(sessionId)
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            authRepository.updateAuthSessions()
            isRefreshing.value = false
        }
    }
}
