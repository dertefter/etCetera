package com.dertefter.settings_theme.presentation

import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor

data class UiState(
    val emojiAvatarHarmonizeColor: EmojiAvatarHarmonizationColor,
    val darkTheme: Boolean?,
    val postHorizontalExtraSpace: Boolean,
    val postContained: Boolean,
    val postShowUsername: Boolean,
    val postSwapDateAndUsername: Boolean,

    val navFloating: Boolean,
    val navLabeled: Boolean,
    val navBlurred: Boolean,
    val appBarBlurred: Boolean = true,
    val appBarFaded: Boolean? = null,

)
