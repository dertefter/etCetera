@file:Suppress("UnusedReceiverParameter")

package com.dertefter.design.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.materialkolor.ktx.harmonize

val LocalIsFold = staticCompositionLocalOf { false }

val LocalIsDark = staticCompositionLocalOf { false }

val LocalIsTab = staticCompositionLocalOf { false }
val LocalIsBlurSupported = staticCompositionLocalOf { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S }

val LocalEmojiAvatarHarmonizationColor = staticCompositionLocalOf { EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER }

val LocalPostHorizontalExtraSpace = staticCompositionLocalOf { true }
val LocalPostContained = staticCompositionLocalOf { true }
val LocalAttachmentsHorizontalExtraSpace = staticCompositionLocalOf { true }
val LocalAttachmentsIsCarousel = staticCompositionLocalOf { true }
val LocalPostShowUsername = staticCompositionLocalOf { true }
val LocalPostSwapDateAndUsername = staticCompositionLocalOf { false }
val LocalBottomNavHeight = compositionLocalOf { 0.dp }

val LocalNavFloating = staticCompositionLocalOf { true }
val LocalNavLabeled = staticCompositionLocalOf { false }
val LocalNavBlurred = staticCompositionLocalOf { true }
val LocalAppBarBlurred = staticCompositionLocalOf { true }
val LocalAppBarFaded = staticCompositionLocalOf<Boolean?> { null }

@Immutable
data class CustomColors(
    val likeColor: Color = Color.Unspecified,
    val onLikeColor: Color = Color.Unspecified,
    val likeContainerColor: Color = Color.Unspecified,
    val onLikeContainerColor: Color = Color.Unspecified,
)


private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
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

val MaterialTheme.postHorizontalExtraSpace: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalPostHorizontalExtraSpace.current

val MaterialTheme.postContained: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalPostContained.current

val MaterialTheme.attachmentsHorizontalExtraSpace: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalAttachmentsHorizontalExtraSpace.current

val MaterialTheme.attachmentsIsCarousel: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalAttachmentsIsCarousel.current

val MaterialTheme.postShowUsername: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalPostShowUsername.current

val MaterialTheme.postSwapDateAndUsername: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalPostSwapDateAndUsername.current

val MaterialTheme.bottomNavHeight: Dp
    @Composable
    @ReadOnlyComposable
    get() = LocalBottomNavHeight.current

val MaterialTheme.navFloating: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalNavFloating.current

val MaterialTheme.navLabeled: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalNavLabeled.current

val MaterialTheme.navBlurred: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalNavBlurred.current

val MaterialTheme.appBarBlurred: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalAppBarBlurred.current

val MaterialTheme.appBarFaded: Boolean?
    @Composable
    @ReadOnlyComposable
    get() = LocalAppBarFaded.current

val MaterialTheme.isBlurSupported: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalIsBlurSupported.current


enum class EmojiAvatarHarmonizationColor {
    DEFAULT,
    PRIMARY,
    SECONDARY,
    TERTIARY,
    SURFACE_CONTAINER,
    PRIMARY_CONTAINER,
    SECONDARY_CONTAINER,
    TERTIARY_CONTAINER
}





@Composable
fun AppTheme(
    darkTheme: Boolean? = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    emojiAvatarHarmonizeColor: EmojiAvatarHarmonizationColor = EmojiAvatarHarmonizationColor.DEFAULT,
    postHorizontalExtraSpace: Boolean = true,
    postContained: Boolean = true,
    attachmentsHorizontalExtraSpace: Boolean = true,
    attachmentsIsCarousel: Boolean = true,
    postShowUsername: Boolean = true,
    postSwapDateAndUsername: Boolean = false,
    bottomNavHeight: Dp = 0.dp,
    navFloating: Boolean = true,
    navLabeled: Boolean = false,
    navBlurred: Boolean = true,
    appBarBlurred: Boolean = true,
    appBarFaded: Boolean? = null,
    content: @Composable () -> Unit,
) {


    val darkTheme = darkTheme ?: isSystemInDarkTheme()

    val isBlurSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

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

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }

    val likeSourceColor = Color(0xFFF53585)

    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalRounding provides Rounding(),
        LocalIsFold provides isFold,
        LocalIsTab provides isTab,
        LocalIsDark provides darkTheme,
        LocalIsBlurSupported provides isBlurSupported,
        LocalEmojiAvatarHarmonizationColor provides emojiAvatarHarmonizeColor,
        LocalPostHorizontalExtraSpace provides postHorizontalExtraSpace,
        LocalPostContained provides postContained,
        LocalAttachmentsHorizontalExtraSpace provides attachmentsHorizontalExtraSpace,
        LocalAttachmentsIsCarousel provides attachmentsIsCarousel,
        LocalPostShowUsername provides postShowUsername,
        LocalPostSwapDateAndUsername provides postSwapDateAndUsername,
        LocalBottomNavHeight provides bottomNavHeight,
        LocalNavFloating provides navFloating,
        LocalNavLabeled provides navLabeled,
        LocalNavBlurred provides (navBlurred && isBlurSupported),
        LocalAppBarBlurred provides (appBarBlurred && isBlurSupported),
        LocalAppBarFaded provides appBarFaded,
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
