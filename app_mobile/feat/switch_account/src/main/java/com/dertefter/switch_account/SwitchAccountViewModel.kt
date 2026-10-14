package com.dertefter.switch_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import com.dertefter.data.repository.AuthRepository
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.switch_account.presentation.Event
import com.dertefter.switch_account.presentation.UiState
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class SwitchAccountViewModel(
    private val authRepository: AuthRepository,
    private val navigator: Navigator
) : ViewModel() {

    val uiState: StateFlow<UiState> = combine(
        authRepository.loginHistory,
        authRepository.currentLogin
    ) { loginHistory, currentLogin ->
        UiState(
            loginHistory = loginHistory,
            currentLogin = currentLogin
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState()
    )

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnSwitchAccount -> {
                viewModelScope.launch {
                    authRepository.switchToLogin(event.login)
                    navigator.hideBottomSheet()
                }
            }

            Event.OnAddAccount -> {
                navigator.navigate(Routes.Auth)
            }

            is Event.OnRemoveAccountFromHistory -> {
                viewModelScope.launch {
                    authRepository.removeLoginFromHistory(event.login)
                }
            }

            Event.OnNavigateBack -> {
                navigator.hideBottomSheet()
            }
        }
    }
}
