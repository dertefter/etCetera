package com.dertefter.settings_theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor
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

    private val _postHorizontalExtraSpace = settingsRepository.postHorizontalExtraSpace

    private val _postContained = settingsRepository.postContained

    private val _postShowUsername = settingsRepository.postShowUsername

    private val _postSwapDateAndUsername = settingsRepository.postSwapDateAndUsername

    private val _navFloating = settingsRepository.navFloating

    private val _navLabeled = settingsRepository.navLabeled

    private val _navBlurred = settingsRepository.navBlurred

    private val _appBarBlurred = settingsRepository.appBarBlurred

    private val _appBarFaded = settingsRepository.appBarFaded

    val uiState = combine(
        _emojiAvatarHarmonizationColor,
        _darkTheme,
        _postHorizontalExtraSpace,
        _postContained,
        _postShowUsername,
        _postSwapDateAndUsername,
        _navFloating,
        _navLabeled,
        _navBlurred,
        _appBarBlurred,
        _appBarFaded
    ) { params: Array<Any?> ->
        val color = params[0] as EmojiAvatarHarmonizationColor
        val darkTheme = params[1] as Boolean?
        val postHorizontalExtraSpace = params[2] as Boolean
        val postContained = params[3] as Boolean
        val postShowUsername = params[4] as Boolean
        val postSwapDateAndUsername = params[5] as Boolean
        val navFloating = params[6] as Boolean
        val navLabeled = params[7] as Boolean
        val navBlurred = params[8] as Boolean
        val appBarBlurred = params[9] as Boolean
        val appBarFaded = params[10] as Boolean?
        UiState(color, darkTheme, postHorizontalExtraSpace, postContained, postShowUsername, postSwapDateAndUsername, navFloating, navLabeled, navBlurred, appBarBlurred, appBarFaded)
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

            is Event.OnUpdatePostHorizontalExtraSpace -> {
                viewModelScope.launch {
                    settingsRepository.updatePostHorizontalExtraSpace(event.value)
                }
            }

            is Event.OnUpdatePostContained -> {
                viewModelScope.launch {
                    settingsRepository.updatePostContained(event.value)
                }
            }

            is Event.OnUpdatePostShowUsername -> {
                viewModelScope.launch {
                    settingsRepository.updatePostShowUsername(event.value)
                }
            }

            is Event.OnUpdatePostSwapDateAndUsername -> {
                viewModelScope.launch {
                    settingsRepository.updatePostSwapDateAndUsername(event.value)
                }
            }

            is Event.OnUpdateNavFloating -> {
                viewModelScope.launch {
                    settingsRepository.updateNavFloating(event.value)
                }
            }

            is Event.OnUpdateNavLabeled -> {
                viewModelScope.launch {
                    settingsRepository.updateNavLabeled(event.value)
                }
            }

            is Event.OnUpdateNavBlurred -> {
                viewModelScope.launch {
                    settingsRepository.updateNavBlurred(event.value)
                }
            }

            is Event.OnUpdateAppBarBlurred -> {
                viewModelScope.launch {
                    settingsRepository.updateAppBarBlurred(event.value)
                }
            }

            is Event.OnUpdateAppBarFaded -> {
                viewModelScope.launch {
                    settingsRepository.updateAppBarFaded(event.value)
                }
            }
        }
    }
}
