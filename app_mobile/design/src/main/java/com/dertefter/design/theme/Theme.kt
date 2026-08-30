package com.dertefter.design.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.materialkolor.ktx.harmonize

private val DarkColorScheme = darkColorScheme()

private val LightColorScheme = lightColorScheme()

val LocalIsFold = staticCompositionLocalOf { false }

val LocalIsDark = staticCompositionLocalOf { false }

val LocalIsTab = staticCompositionLocalOf { false }

val LocalEmojiAvatarHarmonizationColor = staticCompositionLocalOf { EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER }

@Immutable
data class CustomColors(
    val likeColor: Color = Color.Unspecified,
    val onLikeColor: Color = Color.Unspecified,
    val likeContainerColor: Color = Color.Unspecified,
    val onLikeContainerColor: Color = Color.Unspecified,
)

val LocalCustomColors = staticCompositionLocalOf { CustomColors() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current

val MaterialTheme.rounding: Rounding
    @Composable
    @ReadOnlyComposable
    get() = LocalRounding.current

val MaterialTheme.customColors: CustomColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current

val MaterialTheme.emojiAvatarHarmonizeColor: EmojiAvatarHarmonizationColor
    @Composable
    @ReadOnlyComposable
    get() = LocalEmojiAvatarHarmonizationColor.current

val MaterialTheme.isFold: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalIsFold.current

val MaterialTheme.isDark: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalIsDark.current

val MaterialTheme.isTab: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalIsTab.current


enum class EmojiAvatarHarmonizationColor {
    PRIMARY,
    SECONDARY,
    TERTIARY,
    SURFACE_CONTAINER,
    PRIMARY_CONTAINER,
    SECONDARY_CONTAINER,
    TERTIARY_CONTAINER
}





@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppTheme(
    darkTheme: Boolean? = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    emojiAvatarHarmonizeColor: EmojiAvatarHarmonizationColor = EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER,
    content: @Composable () -> Unit
) {


    val darkTheme = darkTheme ?: isSystemInDarkTheme()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }


    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val widthInDp = with(density) { windowInfo.containerSize.width.toDp() }

    val isFold = widthInDp > 600.dp
    val isTab = widthInDp > 1200.dp

    val context = LocalContext.current

    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (darkTheme) {
            dynamicDarkColorScheme(context)
        } else {
            dynamicLightColorScheme(context)
        }
    } else {
        if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        }
    }

    val likeSourceColor = Color(0xFFFA237E)

    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalRounding provides Rounding(),
        LocalIsFold provides isFold,
        LocalIsTab provides isTab,
        LocalIsDark provides darkTheme,
        LocalEmojiAvatarHarmonizationColor provides emojiAvatarHarmonizeColor,
    ) {
        MaterialExpressiveTheme (
            motionScheme = MotionScheme.expressive(),
            typography = Typography,
            colorScheme = colorScheme
        ) {
            val likeColor =
                likeSourceColor.harmonize(
                    MaterialTheme.colorScheme.primary,
                    matchSaturation = true
                )

            val onLikeColor =
                likeSourceColor.harmonize(
                    MaterialTheme.colorScheme.onPrimary,
                    matchSaturation = true
                )

            val likeContainerColor =
                likeSourceColor.harmonize(
                    MaterialTheme.colorScheme.primaryContainer,
                    matchSaturation = true
                )

            val onLikeContainerColor =
                likeSourceColor.harmonize(
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    matchSaturation = true
                )


            val customColors =
                remember(likeColor, onLikeColor, likeContainerColor, onLikeContainerColor, emojiAvatarHarmonizeColor) {
                    CustomColors(
                        likeColor = likeColor,
                        onLikeColor = onLikeColor,
                        likeContainerColor = likeContainerColor,
                        onLikeContainerColor = onLikeContainerColor
                    )
                }
            CompositionLocalProvider(
                LocalCustomColors provides customColors,
                content = content
            )
        }
    }
}
