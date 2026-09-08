package com.dertefter.settings_theme.presentation

import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor
import com.dertefter.navigation.Routes

sealed interface Event {

    data object OnNavigateBack : Event
    data class OnNavigateTo(val route: Routes) : Event
    data class OnUpdateEmojiAvatarHarmonizationColor(val color: EmojiAvatarHarmonizationColor) : Event

    data class OnUpdateDarkTheme(val darkTheme: Boolean?) : Event

    data class OnUpdatePostHorizontalExtraSpace(val value: Boolean) : Event
    data class OnUpdatePostContained(val value: Boolean) : Event
    data class OnUpdatePostShowUsername(val value: Boolean) : Event
    data class OnUpdatePostSwapDateAndUsername(val value: Boolean) : Event

}
