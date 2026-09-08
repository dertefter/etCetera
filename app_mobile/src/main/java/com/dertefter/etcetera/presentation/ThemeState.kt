package com.dertefter.etcetera.presentation

import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor

data class ThemeState(
    val emojiHarmonizationColor: EmojiAvatarHarmonizationColor,
    val darkTheme: Boolean?,
    val postHorizontalExtraSpace: Boolean,
    val postContained: Boolean,
    val postShowUsername: Boolean,
    val postSwapDateAndUsername: Boolean
)
