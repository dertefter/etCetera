package com.dertefter.settings_about.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ChipItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val action: () -> Unit = {}
)