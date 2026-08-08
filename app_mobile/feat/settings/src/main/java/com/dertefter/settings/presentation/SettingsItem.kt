package com.dertefter.settings.presentation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.dertefter.navigation.Routes

data class SettingsItem(
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int? = null,
    val icon: ImageVector,
    val route: Routes
)
