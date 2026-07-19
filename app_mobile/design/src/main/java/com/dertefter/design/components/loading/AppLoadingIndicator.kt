package com.dertefter.design.components.loading

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LoadingIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppLoadingIndicator(
    modifier: Modifier = Modifier,
    progress: () -> Float = { -1f },
    color: Color = LoadingIndicatorDefaults.indicatorColor
) {
    val isIndeterminate = progress() == -1f

    if (!isIndeterminate){
        LoadingIndicator(progress = progress, color = color,modifier = modifier)
    } else {
        LoadingIndicator(color = color, modifier = modifier)
    }
}