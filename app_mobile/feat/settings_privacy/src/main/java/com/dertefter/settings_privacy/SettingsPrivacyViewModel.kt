package com.dertefter.settings_privacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.me.UpdatePrivacyRequestDto
import com.dertefter.data.repository.MeRepository
import com.dertefter.navigation.Navigator
import com.dertefter.settings_privacy.presentation.Event
import com.dertefter.settings_privacy.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsPrivacyViewModel @Inject constructor(
    private val navigator: Navigator,
    private val meRepository: MeRepository
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<UiState> = combine(
        meRepository.privacy,
        isRefreshing
    ) { privacy, refreshing ->
        UiState(
            isLoading = refreshing,
            privacy = privacy
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState(isLoading = true, privacy = null)
    )

    init {
        refresh()
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.OnNavigateBack -> navigator.navigateUp()
            Event.OnRefresh -> refresh()
            is Event.ChangeIsPrivate -> updatePrivacy(UpdatePrivacyRequestDto(isPrivate = event.value))
            is Event.ChangeWallAccess -> updatePrivacy(UpdatePrivacyRequestDto(wallAccess = event.value))
            is Event.ChangeLikesVisibility -> updatePrivacy(UpdatePrivacyRequestDto(likesVisibility = event.value))
            is Event.ChangeMessageAccess -> updatePrivacy(UpdatePrivacyRequestDto(messageAccess = event.value))
            is Event.ChangeShowLastSeen -> updatePrivacy(UpdatePrivacyRequestDto(showLastSeen = event.value))
        }
    }

    private fun updatePrivacy(request: UpdatePrivacyRequestDto) {
        viewModelScope.launch {
            meRepository.savePrivacy(request)
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            meRepository.updatePrivacy()
            isRefreshing.value = false
        }
    }
}
