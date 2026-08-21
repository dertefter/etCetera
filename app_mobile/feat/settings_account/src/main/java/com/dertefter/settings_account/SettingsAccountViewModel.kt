package com.dertefter.settings_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.repository.MeRepository
import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.navigation.Navigator
import com.dertefter.settings_account.presentation.Event
import com.dertefter.settings_account.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsAccountViewModel @Inject constructor(
    private val navigator: Navigator,
    private val meRepository: MeRepository
) : ViewModel() {

    private val _me = meRepository.meDto
    private val _isLoading = MutableStateFlow(false)
    private val _displayName = MutableStateFlow("")
    private val _username = MutableStateFlow("")
    private val _bio = MutableStateFlow("")

    val uiState = combine(
        _me,
        _isLoading,
        _displayName,
        _username,
        _bio
    ) { me, isLoading, displayName, username, bio ->
        val canSave = !isLoading && me != null && (
                displayName != me.displayName ||
                        username != me.username ||
                        bio != (me.bio ?: "")
                )
        UiState(
            me = me,
            isLoading = isLoading,
            canSave = canSave,
            displayNameInput = displayName,
            usernameInput = username,
            bioInput = bio
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState()
    )

    init {
        fetchMe()
        viewModelScope.launch {
            _me.collectLatest { me ->
                me?.let {
                    _displayName.value = it.displayName
                    _username.value = it.username
                    _bio.value = it.bio ?: ""
                }
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnRefresh -> {
                fetchMe()
            }

            is Event.OnNavigateBack -> {
                navigator.navigateUp()
            }

            is Event.OnDisplayNameChange -> {
                _displayName.value = event.value
            }

            is Event.OnUsernameChange -> {
                _username.value = event.value
            }

            is Event.OnBioChange -> {
                _bio.value = event.value
            }

            is Event.OnSave -> {
                saveChanges()
            }
        }
    }

    private fun saveChanges() {
        viewModelScope.launch {
            _isLoading.value = true
            val request = UpdateMeRequestDto(
                displayName = _displayName.value,
                username = _username.value,
                bio = _bio.value
            )
            meRepository.updateMe(request)
            _isLoading.value = false
        }
    }

    private fun fetchMe() {
        viewModelScope.launch {
            _isLoading.value = true
            meRepository.fetchMe()
            _isLoading.value = false
        }
    }
}
