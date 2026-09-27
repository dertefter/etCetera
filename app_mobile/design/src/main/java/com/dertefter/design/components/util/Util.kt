package com.dertefter.design.components.util

import androidx.compose.animation.core.Animatable
import androidx.compose.material3.TopAppBarState

suspend fun TopAppBarState.collapse() {
    animateHeightOffsetTo(heightOffsetLimit)
}

suspend fun TopAppBarState.expand() {
    animateHeightOffsetTo(0f)
}

private suspend fun TopAppBarState.animateHeightOffsetTo(target: Float) {
    Animatable(heightOffset).animateTo(target) {
        heightOffset = value
    }
}