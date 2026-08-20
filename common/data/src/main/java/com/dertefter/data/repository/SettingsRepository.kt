package com.dertefter.data.repository

import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val emojiAvatarHarmonizationColor: Flow<EmojiAvatarHarmonizationColor>

    suspend fun updateEmojiAvatarHarmonizationColor(color: EmojiAvatarHarmonizationColor)

    val darkTheme: Flow<Boolean?>

    suspend fun updateDarkTheme(darkTheme: Boolean?)

}