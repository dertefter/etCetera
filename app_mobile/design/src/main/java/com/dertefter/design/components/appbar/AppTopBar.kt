package com.dertefter.design.components.appbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.dertefter.design.theme.appBarBlurred
import com.dertefter.design.theme.appBarFaded
import com.dertefter.design.theme.isFold
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.blur.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState


enum class AppTopBarStyle {
    SMALL, MEDIUM, LARGE
}

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    appTopBarStyle: AppTopBarStyle = AppTopBarStyle.LARGE,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: @Composable (() -> Unit),
    subtitle: @Composable (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit) = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    supportingContent: @Composable (ColumnScope.() -> Unit) = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    hazeState: HazeState = rememberHazeState(),
) {

    val appBarBlurred = MaterialTheme.appBarBlurred
    val appBarFaded = MaterialTheme.appBarFaded ?: MaterialTheme.isFold
    val appBarCollaption = scrollBehavior?.state?.collapsedFraction ?: 0f

    val blurEffectAlpha  = scrollBehavior?.state?.overlappedFraction ?: 0f

    val appBarBackgroundColor = if (appBarFaded) {
        colors.containerColor.copy(alpha = 0f)
    } else {
        lerp(colors.containerColor, colors.scrolledContainerColor, appBarCollaption)
    }

    val hazeStyle = if (appBarFaded) {
        HazeBlurStyle {
            progressive(
                HazeProgressive.verticalGradient(
                    startIntensity = 1f,
                    endIntensity = 0f,
                ),
            )
        }
    } else {
        HazeMaterials.ultraThin(
            containerColor = appBarBackgroundColor
        ).then {
            alpha(blurEffectAlpha)
            blurRadius(44.dp)
        }
    }

    Column(
        modifier = modifier
            .then(
                if (appBarBlurred) {
                    Modifier
                        .hazeBlur(
                            input = HazeInput.Sources(hazeState),
                            style = hazeStyle,
                        )
                } else {
                    Modifier
                }
            )
            .then(
                if (appBarFaded){
                    Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.99f),
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.97f),
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.82f),
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.64f),
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.29f),
                                    MaterialTheme.colorScheme.background.copy(alpha = 0f),
                                )
                            )
                        )
                }
                else if (!appBarBlurred) {
                    Modifier
                        .background(appBarBackgroundColor)
                } else {
                    Modifier
                }
            )
            .displayCutoutPadding()

    ) {

        when (appTopBarStyle) {
            AppTopBarStyle.SMALL -> {
                TopAppBar(
                    colors = colors.copy(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                    title = title,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    scrollBehavior = scrollBehavior,
                )
            }

            AppTopBarStyle.MEDIUM -> {
                MediumFlexibleTopAppBar(
                    colors = colors.copy(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                    title = title,
                    subtitle = subtitle,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    scrollBehavior = scrollBehavior,
                )
            }

            AppTopBarStyle.LARGE -> {
                LargeFlexibleTopAppBar(
                    colors = colors.copy(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                    title = title,
                    subtitle = subtitle,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    scrollBehavior = scrollBehavior,
                )
            }
        }

        supportingContent()
    }

}