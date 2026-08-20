package com.dertefter.data.repository

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
) : SettingsRepository {

    override val emojiAvatarHarmonizationColor: Flow<EmojiAvatarHarmonizationColor> =
        localDataSource.emojiAvatarHarmonizationColor.map { colorName ->
            colorName?.let {
                try {
                    EmojiAvatarHarmonizationColor.valueOf(it)
                } catch (_: IllegalArgumentException) {
                    EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER
                }
            } ?: EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER
        }

    override suspend fun updateEmojiAvatarHarmonizationColor(color: EmojiAvatarHarmonizationColor) {
        localDataSource.updateEmojiAvatarHarmonizationColor(color.name)
    }

    override val darkTheme: Flow<Boolean?> = localDataSource.darkTheme

    override suspend fun updateDarkTheme(darkTheme: Boolean?) {
        localDataSource.updateDarkTheme(darkTheme)
    }
}
