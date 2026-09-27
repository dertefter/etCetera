package com.dertefter.data.repository

import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val emojiAvatarHarmonizationColor: Flow<EmojiAvatarHarmonizationColor>

    suspend fun updateEmojiAvatarHarmonizationColor(color: EmojiAvatarHarmonizationColor)

    val darkTheme: Flow<Boolean?>

    suspend fun updateDarkTheme(darkTheme: Boolean?)

    val postHorizontalExtraSpace: Flow<Boolean> // default true
    suspend fun updatePostHorizontalExtraSpace(value: Boolean)

    val postContained: Flow<Boolean> // default true
    suspend fun updatePostContained(value: Boolean)

    val postShowUsername: Flow<Boolean> // default true
    suspend fun updatePostShowUsername(value: Boolean)

    val postSwapDateAndUsername: Flow<Boolean> // default false
    suspend fun updatePostSwapDateAndUsername(value: Boolean)

    val navFloating: Flow<Boolean>
    suspend fun updateNavFloating(value: Boolean)

    val navLabeled: Flow<Boolean>
    suspend fun updateNavLabeled(value: Boolean)

    val navBlurred: Flow<Boolean>
    suspend fun updateNavBlurred(value: Boolean)

    val appBarBlurred: Flow<Boolean>
    suspend fun updateAppBarBlurred(value: Boolean)

    val appBarFaded: Flow<Boolean?>
    suspend fun updateAppBarFaded(value: Boolean?)

}
