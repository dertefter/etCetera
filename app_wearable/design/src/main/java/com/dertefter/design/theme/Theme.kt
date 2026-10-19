@file:Suppress("UnusedReceiverParameter")

package com.dertefter.design.theme

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.MotionScheme
import androidx.wear.compose.material3.dynamicColorScheme
import com.materialkolor.ktx.harmonize


@Immutable
data class CustomColors(
    val success: Color = Color.Unspecified,
    val onSuccess: Color = Color.Unspecified,
    val successContainer: Color = Color.Unspecified,
    val onSuccessContainer: Color = Color.Unspecified,
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


@Composable
fun WearableTheme(
    content: @Composable () -> Unit
) {

    val context = LocalContext.current



    val colorScheme: ColorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicColorScheme(context) ?:  ColorScheme()
    } else {
        ColorScheme()
    }

    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalRounding provides Rounding(),
    ) {
        MaterialTheme(
            motionScheme = MotionScheme.expressive(),
            colorScheme = colorScheme
        ) {
            val successColor =
                Color(0xFFF5006A).harmonize(MaterialTheme.colorScheme.primary, matchSaturation = true)
            val onSuccessColor =
                Color(0xFFF5006A).harmonize(MaterialTheme.colorScheme.onPrimary, matchSaturation = true)
            val successContainer =
                Color(0xFFF5006A).harmonize(
                    MaterialTheme.colorScheme.primaryContainer,
                    matchSaturation = true
                )
            val onSuccessContainer =
                Color(0xFFF5006A).harmonize(
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    matchSaturation = true
                )


            val customColors =
                remember(successColor, onSuccessColor, successContainer, onSuccessContainer) {
                    CustomColors(
                        success = successColor,
                        onSuccess = onSuccessColor,
                        successContainer = successContainer,
                        onSuccessContainer = onSuccessContainer
                    )
                }
            CompositionLocalProvider(
                LocalCustomColors provides customColors,
                content = content
            )
        }
    }
}
