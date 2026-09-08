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

    override val postHorizontalExtraSpace: Flow<Boolean> = localDataSource.postHorizontalExtraSpace

    override suspend fun updatePostHorizontalExtraSpace(value: Boolean) {
        localDataSource.updatePostHorizontalExtraSpace(value)
    }

    override val postContained: Flow<Boolean> = localDataSource.postContained

    override suspend fun updatePostContained(value: Boolean) {
        localDataSource.updatePostContained(value)
    }

    override val postShowUsername: Flow<Boolean> = localDataSource.postShowUsername

    override suspend fun updatePostShowUsername(value: Boolean) {
        localDataSource.updatePostShowUsername(value)
    }

    override val postSwapDateAndUsername: Flow<Boolean> = localDataSource.postSwapDateAndUsername

    override suspend fun updatePostSwapDateAndUsername(value: Boolean) {
        localDataSource.updatePostSwapDateAndUsername(value)
    }
}
