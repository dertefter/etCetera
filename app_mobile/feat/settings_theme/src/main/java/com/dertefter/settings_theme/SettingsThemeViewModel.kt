package com.dertefter.settings_theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.repository.SettingsRepository
import com.dertefter.navigation.Navigator
import com.dertefter.settings_theme.presentation.Event
import com.dertefter.settings_theme.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsThemeViewModel @Inject constructor(
    private val navigator: Navigator,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _emojiAvatarHarmonizationColor = settingsRepository.emojiAvatarHarmonizationColor

    private val _darkTheme = settingsRepository.darkTheme

    val uiState = combine(
        _emojiAvatarHarmonizationColor,
        _darkTheme
    ) { color, darkTheme ->
        UiState(color, darkTheme)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun onEvent(event: Event) {
        when (event) {

            is Event.OnUpdateDarkTheme -> {
                viewModelScope.launch {
                    settingsRepository.updateDarkTheme(event.darkTheme)
                }
            }

            is Event.OnNavigateBack -> {
                navigator.navigateUp()
            }

            is Event.OnNavigateTo -> {
                navigator.navigate(
                    event.route
                )
            }

            is Event.OnUpdateEmojiAvatarHarmonizationColor -> {
                viewModelScope.launch {
                    settingsRepository.updateEmojiAvatarHarmonizationColor(event.color)
                }
            }
        }
    }
}
