package com.dertefter.auth

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.auth.presentation.Event
import com.dertefter.auth.presentation.UiState
import com.dertefter.auth.usecase.SignInUseCase
import com.dertefter.data.common.AppError
import com.dertefter.data.common.toAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _login = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _isPasswordVisible = MutableStateFlow(false)

    private val _isLoading = MutableStateFlow(false)

    private val _error = MutableStateFlow<AppError?>(null)
    private val _isTurnstileVisible = MutableStateFlow(false)


    val state: StateFlow<UiState> = combine(
        _login,
        _password,
        _isPasswordVisible,
        _isLoading,
        _error
    ) { login, password, isPasswordVisible, isLoading, error ->
        UiState(
            login = login,
            isLoginValid = login.isEmpty() || password.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(login).matches(),
            password = password,
            isPasswordVisible = isPasswordVisible,
            isLoading = isLoading,
            error = error,
        )
    }.combine(_isTurnstileVisible) { uiState, isTurnstileVisible ->
        uiState.copy(isTurnstileVisible = isTurnstileVisible)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState()
    )

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnLogout -> {
                viewModelScope.launch {
                    //logoutUseCase()
                }
            }

            is Event.OnLoginChanged -> {
                _login.value = event.login
            }

            is Event.OnPasswordChanged -> {
                _password.value = event.password
            }

            is Event.OnTogglePasswordVisibility -> {
                _isPasswordVisible.value = !_isPasswordVisible.value
            }

            Event.OnSubmit -> {
                _isTurnstileVisible.value = true
            }

            is Event.OnTurnstileTokenReceived -> {
                _isTurnstileVisible.value = false
                submit(event.token)
            }

            Event.OnDismissTurnstile -> {
                _isTurnstileVisible.value = false
            }

            Event.OnNavigateBack -> {}

        }
    }

    private fun submit(turnstileToken: String) {
        viewModelScope.launch {
            val login = _login.value
            val password = _password.value
            _isLoading.value = true
            _error.value = null

            signInUseCase(
                login, password,
                turnstileToken = turnstileToken,
            ).onSuccess {
                Log.d("AUth", "OK")
            }.onFailure {
                Log.e("AUth", it.stackTraceToString())
                _error.value = it.toAppError()
                Log.e("AUth", _error.value?.message.toString())
            }
            _isLoading.value = false
        }
    }
}
