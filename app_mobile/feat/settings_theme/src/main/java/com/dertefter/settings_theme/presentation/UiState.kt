package com.dertefter.settings_theme.presentation

import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor

data class UiState(
    val emojiAvatarHarmonizeColor: EmojiAvatarHarmonizationColor,
    val darkTheme: Boolean?
)
