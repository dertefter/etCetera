package com.dertefter.design.components.loading

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.CircularProgressIndicator


@Composable
fun AppLoadingIndicator(
    modifier: Modifier = Modifier,
){
    CircularProgressIndicator(modifier = modifier)
}

@Composable
fun AppLoadingIndicator(
    modifier: Modifier = Modifier,
    progress: () -> Float
){
    CircularProgressIndicator(modifier = modifier, progress = progress)
}