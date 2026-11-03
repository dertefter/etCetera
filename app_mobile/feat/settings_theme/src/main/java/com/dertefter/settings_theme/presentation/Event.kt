package com.dertefter.settings_theme.presentation

import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor

sealed interface Event {

    data object OnNavigateBack : Event
    data class OnUpdateEmojiAvatarHarmonizationColor(val color: EmojiAvatarHarmonizationColor) : Event
    data class OnUpdateAnimatedEmoji(val value: Boolean) : Event

    data class OnUpdateDarkTheme(val darkTheme: Boolean?) : Event

    data class OnUpdatePostHorizontalExtraSpace(val value: Boolean) : Event
    data class OnUpdatePostContained(val value: Boolean) : Event
    data class OnUpdatePostShowUsername(val value: Boolean) : Event
    data class OnUpdatePostSwapDateAndUsername(val value: Boolean) : Event

    data class OnUpdateNavFloating(val value: Boolean) : Event
    data class OnUpdateNavLabeled(val value: Boolean) : Event
    data class OnUpdateNavBlurred(val value: Boolean) : Event

    data class OnUpdateAppBarBlurred(val value: Boolean) : Event
    data class OnUpdateAppBarFaded(val value: Boolean?) : Event

}
