package com.dertefter.design.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.ktx.harmonize
import com.materialkolor.rememberDynamicMaterialThemeState

private val DarkColorScheme = darkColorScheme()

private val LightColorScheme = lightColorScheme()

val LocalIsFold = staticCompositionLocalOf { false }

val LocalIsTab = staticCompositionLocalOf { false }

val LocalSeedColor = staticCompositionLocalOf<Long?> { null }

val LocalPaletteStyle = staticCompositionLocalOf { PaletteStyle.Vibrant }

val LocalSpecVersion = staticCompositionLocalOf { ColorSpec.SpecVersion.SPEC_2021 }

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

val MaterialTheme.isFold: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalIsFold.current

val MaterialTheme.isTab: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalIsTab.current

val MaterialTheme.seedColor: Long?
    @Composable
    @ReadOnlyComposable
    get() = LocalSeedColor.current

val MaterialTheme.paletteStyle: PaletteStyle
    @Composable
    @ReadOnlyComposable
    get() = LocalPaletteStyle.current

val MaterialTheme.specVersion: ColorSpec.SpecVersion
    @Composable
    @ReadOnlyComposable
    get() = LocalSpecVersion.current



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    paletteStyle: PaletteStyle? = null,
    specVersion: ColorSpec.SpecVersion? = null,
    seedColor: Long? = null,
    isCut: Boolean? = false,
    content: @Composable () -> Unit
) {

    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val widthInDp = with(density) { windowInfo.containerSize.width.toDp() }

    val isFold = widthInDp > 600.dp
    val isTab = widthInDp > 840.dp

    val context = LocalContext.current

    val paletteStyle = paletteStyle ?: PaletteStyle.TonalSpot
    val specVersion = specVersion ?: ColorSpec.SpecVersion.SPEC_2025
    val isCut = isCut ?: false

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

    val dynamicThemeState = if (seedColor == null) {
        rememberDynamicMaterialThemeState(
            isDark = darkTheme,
            style = paletteStyle,
            specVersion = specVersion,
            seedColor = colorScheme.primary,
            primary = colorScheme.primary,
            secondary = colorScheme.secondary,
            tertiary = colorScheme.tertiary,
        )
    } else {
        rememberDynamicMaterialThemeState(
            isDark = darkTheme,
            style = paletteStyle,
            specVersion = specVersion,
            seedColor = Color(seedColor),
        )
    }

    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalRounding provides Rounding(),
        LocalIsFold provides isFold,
        LocalIsTab provides isTab,
        LocalIsCut provides isCut,
        LocalSeedColor provides seedColor,
        LocalPaletteStyle provides paletteStyle,
        LocalSpecVersion provides specVersion
    ) {
        DynamicMaterialExpressiveTheme(
            state = dynamicThemeState,
            motionScheme = MotionScheme.expressive(),
            animate = true,
            typography = Typography,
            shapes = if (isCut) CutShapes else RoundedShapes
        ) {
            val likeColor =
                Color(0xFFF5006A).harmonize(MaterialTheme.colorScheme.primary, matchSaturation = true)
            val onLikeColor =
                Color(0xFFF5006A).harmonize(MaterialTheme.colorScheme.onPrimary, matchSaturation = true)
            val likeContainerColor =
                Color(0xFFF5006A).harmonize(
                    MaterialTheme.colorScheme.primaryContainer,
                    matchSaturation = true
                )
            val onLikeContainerColor =
                Color(0xFFF5006A).harmonize(
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    matchSaturation = true
                )


            val customColors =
                remember(likeColor, onLikeColor, likeContainerColor, onLikeContainerColor) {
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
