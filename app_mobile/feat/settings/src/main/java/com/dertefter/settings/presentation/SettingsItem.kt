package com.dertefter.settings.presentation

import androidx.compose.ui.graphics.vector.ImageVector
import com.dertefter.navigation.Routes


data class SettingsSection(
    val title: String,
    val settingsItems: List<SettingsItem>
)

data class SettingsItem(
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val route: Routes
)
