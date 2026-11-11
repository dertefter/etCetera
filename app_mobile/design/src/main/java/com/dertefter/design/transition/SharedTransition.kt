package com.dertefter.design.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

val LocalSharedTransitionScope: ProvidableCompositionLocal<SharedTransitionScope?> =
    staticCompositionLocalOf { null }

val LocalSharedAnimatedVisibilityScope: ProvidableCompositionLocal<AnimatedVisibilityScope?> =
    staticCompositionLocalOf { null }

@Composable
fun Modifier.sharedElementByKey(key: String?): Modifier {
    if (key.isNullOrEmpty()) return this
    val sharedTransitionScope = LocalSharedTransitionScope.current ?: return this
    val animatedVisibilityScope = LocalSharedAnimatedVisibilityScope.current ?: return this
    return with(sharedTransitionScope) {
        sharedBounds(
            rememberSharedContentState(key = key),
            animatedVisibilityScope = animatedVisibilityScope,
        )
    }
}
