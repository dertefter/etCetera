package com.dertefter.etcetera.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.LocalBottomNavHeight
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.navBlurred
import com.dertefter.design.theme.navFloating
import com.dertefter.design.theme.navLabeled
import com.dertefter.design.theme.spacing
import com.dertefter.navigation.Routes
import com.gigamole.composefadingedges.FadingEdgesGravity
import com.gigamole.composefadingedges.verticalFadingEdges
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.blur.materials.HazeMaterials
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun PhoneUI(
    modifier: Modifier = Modifier,
    activeBackStack: NavBackStack<NavKey>,
    entries: List<NavEntry<NavKey>>,
    selectedTab: MainTab,
    notificationCount: Int?,
    appNavHost: @Composable (
        entries: List<NavEntry<NavKey>>,
        onBack: () -> Unit,
        modifier: Modifier
    ) -> Unit,
    onBack: () -> Unit,
    onNavItemClick: (tab: MainTab) -> Unit
) {

    val hideNav =
        activeBackStack.lastOrNull() is Routes.AttachmentsViewer || activeBackStack.lastOrNull() is Routes.Auth

    val hazeState = rememberHazeState()

    var navBarHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val measuredBottomNavHeight = remember(navBarHeightPx, density) {
        with(density) { navBarHeightPx.toDp() }
    }

    @Composable
    fun labeled(label: String): (@Composable () -> Unit)? =
        if (MaterialTheme.navLabeled) {
            {
                Text(label)
            }
        } else {
            null
        }

    val navCornerSizePercent by animateIntAsState(
        if (MaterialTheme.navFloating) 50 else 0,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )

    val navSpacingHorizontal by animateDpAsState(
        if (MaterialTheme.navFloating) MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.large else 0.dp,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )

    val navSpacingHorizontalInternal by animateDpAsState(
        if (MaterialTheme.navFloating) MaterialTheme.spacing.large else 0.dp,
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
    )


    val navSpacingBottom by animateDpAsState(
        if (MaterialTheme.navFloating) MaterialTheme.spacing.small + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        else 0.dp,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )

    val navSpacingBottomInternal by animateDpAsState(
        if (!MaterialTheme.navFloating) WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        else 0.dp,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    Box(modifier = modifier.fillMaxSize()) {

        CompositionLocalProvider(
            LocalBottomNavHeight provides
                    if (hideNav) 0.dp
                    else max(measuredBottomNavHeight, WindowInsets.ime.asPaddingValues().calculateBottomPadding())
        ) {
            appNavHost(
                entries,
                onBack,
                Modifier
                    .hazeSource(hazeState)
                    .verticalFadingEdges(
                        gravity = FadingEdgesGravity.End, length = MaterialTheme.bottomNavHeight
                    ).then(
                        if (!hideNav){
                            Modifier.consumeWindowInsets(
                                WindowInsets.navigationBars
                            )
                        } else Modifier
                    )


            )
        }

        AnimatedVisibility(
            visible = !hideNav,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            Surface(
                color =  MaterialTheme.colorScheme.surfaceContainer,
                shadowElevation = navSpacingHorizontal.coerceAtLeast(0.dp),
                shape =RoundedCornerShape(navCornerSizePercent.coerceIn(0,100)),
                modifier = Modifier
                    .onSizeChanged { size ->
                        navBarHeightPx = size.height
                    }
                    .padding(top = MaterialTheme.spacing.small)
                    .padding(
                        horizontal = navSpacingHorizontal.coerceAtLeast(0.dp),
                    )
                    .padding(bottom = navSpacingBottom.coerceAtLeast(0.dp))

                    .consumeWindowInsets(WindowInsets.navigationBars)
            ){
                NavigationBar(
                    containerColor = Color.Transparent,
                    modifier = Modifier
                        .then(
                            if (MaterialTheme.navBlurred){
                                Modifier.hazeBlur(
                                    input = HazeInput.Backdrop(hazeState),
                                    style = HazeMaterials.thin(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                                    ),
                                )
                            } else Modifier
                        )
                        .padding(horizontal = navSpacingHorizontalInternal.coerceAtLeast(0.dp))
                        .padding(bottom = navSpacingBottomInternal.coerceAtLeast(0.dp))
                        .fillMaxWidth()
                    )
                {
                    MainTab.entries.forEach { tab ->
                        val selected = selectedTab == tab
                        NavigationBarItem(
                            selected = selected,
                            onClick = { onNavItemClick(tab) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (tab == MainTab.Notifications && notificationCount != null && notificationCount > 0) {
                                            Badge {
                                                Text(notificationCount.toString())
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon() else tab.icon(),
                                        contentDescription = stringResource(tab.label)
                                    )
                                }
                            },
                            label =  labeled(
                                label = stringResource(tab.label)
                            )
                        )
                    }
                }
            }

        }

    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PhoneUIPreview() {
    AppTheme(
        content = {
            val backStack: NavBackStack<NavKey> = rememberNavBackStack(Routes.Feed)
            val entries: List<NavEntry<NavKey>> = listOf(
                NavEntry(Routes.Feed) {
                    Box(modifier = Modifier.fillMaxSize())
                }
            )
            PhoneUI(
                activeBackStack = backStack,
                entries = entries,
                selectedTab = MainTab.Feed,
                notificationCount = 3,
                appNavHost = { _, _, modifier ->
                    Scaffold(
                        modifier = modifier
                            .fillMaxSize(),
                        floatingActionButton = {
                            FloatingActionButton(
                                modifier = Modifier.imePadding(),
                                onClick = {},

                                ) { }
                        }
                    ) { contentPadding ->
                        Box(
                            modifier = modifier
                                .padding(contentPadding)
                                .meshGradient()
                                .fillMaxSize()
                        )
                    }

                },
                onBack = {},
                onNavItemClick = {}
            )
        },
    )
}

@Composable
private fun Modifier.meshGradient(): Modifier {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val secondary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer

    return this.drawWithCache {
        val radius = maxOf(size.width, size.height) * 1.2f

        val brushTopRight = Brush.radialGradient(
            colors = listOf(tertiary, tertiary.copy(alpha = 0f)),
            center = Offset(size.width, 0f),
            radius = radius
        )
        val brushBottomLeft = Brush.radialGradient(
            colors = listOf(secondary, secondary.copy(alpha = 0f)),
            center = Offset(0f, size.height),
            radius = radius
        )
        val brushBottomRight = Brush.radialGradient(
            colors = listOf(primaryContainer, primaryContainer.copy(alpha = 0f)),
            center = Offset(size.width, size.height),
            radius = radius
        )

        onDrawBehind {
            drawRect(color = primary)
            drawRect(brush = brushTopRight)
            drawRect(brush = brushBottomLeft)
            drawRect(brush = brushBottomRight)
        }
    }
}
