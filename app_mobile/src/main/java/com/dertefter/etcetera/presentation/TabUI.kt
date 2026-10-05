package com.dertefter.etcetera.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import coil3.compose.AsyncImage
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.isTab
import com.dertefter.design.theme.navBlurred
import com.dertefter.design.theme.navFloating
import com.dertefter.design.theme.navLabeled
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing
import com.dertefter.navigation.Routes
import com.gigamole.composefadingedges.FadingEdgesGravity
import com.gigamole.composefadingedges.fill.FadingEdgesFillType
import com.gigamole.composefadingedges.verticalFadingEdges
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.blur.materials.HazeMaterials
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun TabUI(
    modifier: Modifier = Modifier,
    activeBackStack: NavBackStack<NavKey>,
    entries: List<NavEntry<NavKey>>,
    selectedTab: MainTab,
    notificationCount: Int?,
    isPortalVisible: Boolean = false,
    appNavHost: @Composable (
        entries: List<NavEntry<NavKey>>,
        onBack: () -> Unit,
        modifier: Modifier
    ) -> Unit,
    onBack: () -> Unit,
    onNavItemClick: (tab: MainTab) -> Unit
) {




    val hideNav = activeBackStack.lastOrNull() is Routes.AttachmentsViewer || activeBackStack.lastOrNull() is Routes.Auth




    TabUIStateless(
        modifier = modifier,
        hideNav = hideNav,
        selectedTab = selectedTab,
        notificationCount = notificationCount,
        isPortalVisible = isPortalVisible,
        onNavItemClick = onNavItemClick,
        content = {
            appNavHost(
                entries,
                onBack,
                Modifier
                    .fillMaxSize()
            )
        }
    )
}

@Composable
fun TabUIStateless(
    modifier: Modifier = Modifier,
    hideNav: Boolean,
    selectedTab: MainTab,
    notificationCount: Int?,
    isPortalVisible: Boolean = false,
    onNavItemClick: (tab: MainTab) -> Unit,
    content: @Composable () -> Unit
) {
    val isTab = MaterialTheme.isTab
    val navLabeled = MaterialTheme.navLabeled
    val railState = rememberWideNavigationRailState(
        initialValue = if
                (isTab && navLabeled)
            WideNavigationRailValue.Expanded
        else WideNavigationRailValue.Collapsed
    )

    LaunchedEffect(isTab && navLabeled) {
        if (isTab && navLabeled) {
            railState.expand()
        } else {
            railState.collapse()
        }
    }

    val hazeState = rememberHazeState()

    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val topLeftCornerRadius by animateDpAsState(
        if (!MaterialTheme.navFloating) MaterialTheme.rounding.extraLarge else 0.dp
    )

    val consumedPaddingValues = if (hideNav) PaddingValues(0.dp) else PaddingValues(
        start = WindowInsets.displayCutout.asPaddingValues().calculateStartPadding(
            LocalLayoutDirection.current
        ), 0.dp,0.dp,0.dp
    )


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
        if (MaterialTheme.navFloating) {
            if (railState.currentValue == WideNavigationRailValue.Collapsed) 50 else 25
        } else 0,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )

    val navSpacingTop by animateDpAsState(
        if (MaterialTheme.navFloating) {
            WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + MaterialTheme.spacing.extraLarge
        } else 0.dp,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    val navSpacingBottom by animateDpAsState(
        if (MaterialTheme.navFloating) {
            WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + MaterialTheme.spacing.extraLarge
        } else 0.dp,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    val navSpacingStart by animateDpAsState(
        if (MaterialTheme.navFloating) MaterialTheme.spacing.defaultScreenPadding
                + WindowInsets.displayCutout.asPaddingValues().calculateStartPadding(LocalLayoutDirection.current)
        else 0.dp,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    val navSpacingEnd by animateDpAsState(
        if (MaterialTheme.navFloating) 0.dp
        else 0.dp,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    val navSpacingStartInternal by animateDpAsState(
        if (!MaterialTheme.navFloating) WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        else 0.dp,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    val backgroundColor by animateColorAsState(
        if (!MaterialTheme.navFloating) MaterialTheme.colorScheme.surfaceContainerHigh
        else MaterialTheme.colorScheme.background
    )

    val navBackgroundColor by animateColorAsState(
        if (MaterialTheme.navFloating) MaterialTheme.colorScheme.surfaceContainer
        else MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0f)
    )

    var navBarWidthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val measuredBottomNavWidth = remember(navBarWidthPx, density) {
        with(density) { navBarWidthPx.toDp() }
    }


    Box(modifier
        .background(backgroundColor)
        .fillMaxSize()
    ){


        Box(
            modifier = Modifier
                .then(
                    if (MaterialTheme.navBlurred && !MaterialTheme.navFloating){
                        Modifier.hazeBlur(
                            input = HazeInput.Backdrop(hazeState),
                            style = HazeMaterials.thin(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                        )
                    } else Modifier
                )
                .fillMaxSize()
        ){

        }

        Box(Modifier
            .hazeSource(hazeState)
            .then(
                if (!hideNav) {
                    Modifier.verticalFadingEdges(
                        fillType = FadingEdgesFillType.FadeColor(
                            color = MaterialTheme.colorScheme.background
                        ),
                        gravity = FadingEdgesGravity.End,
                        length = navigationBarHeight + 12.dp
                    ).padding(start = measuredBottomNavWidth)
                } else Modifier
            )
            .consumeWindowInsets(consumedPaddingValues)
            .clipToBounds()
            .then(
                if (!hideNav && !MaterialTheme.navFloating) {
                    Modifier
                        .statusBarsPadding()
                        .clip(RoundedCornerShape(topStart = topLeftCornerRadius))
                } else Modifier
            )
            .fillMaxSize()
            ) {
            content()
        }

        if (!hideNav) {
            Surface(
                shape = RoundedCornerShape(navCornerSizePercent.coerceIn(0, 100)),
                color = navBackgroundColor,
                shadowElevation = navSpacingStart,
                modifier = Modifier
                    .onSizeChanged { size ->
                        navBarWidthPx = size.width
                    }
                    .padding(
                        top = navSpacingTop.coerceAtLeast(0.dp),
                        bottom = navSpacingBottom.coerceAtLeast(0.dp),
                        start = navSpacingStart.coerceAtLeast(0.dp),
                        end = navSpacingEnd.coerceAtLeast(0.dp)
                    )

            ){
                WideNavigationRail(
                    modifier = Modifier
                        .then(
                            if (MaterialTheme.navBlurred && MaterialTheme.navFloating){
                                Modifier.hazeBlur(
                                    input = HazeInput.Backdrop(hazeState),
                                    style = HazeMaterials.ultraThin(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    ).then { blurRadius(88.dp) },
                                )
                            } else Modifier
                        )
                        .then(
                            if (MaterialTheme.navFloating){
                                Modifier
                                    .padding(
                                        start = navSpacingStartInternal.coerceAtLeast(0.dp)
                                    )
                                    .consumeWindowInsets(WindowInsets.safeContent)
                            } else Modifier
                        )
                    ,
                    state = railState,
                    colors = WideNavigationRailDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(
                        vertical = MaterialTheme.spacing.extraLarge,
                        horizontal = 0.dp
                    ),
                    arrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)

                )
                {
                    MainTab.entries.forEach { tab ->

                        val selected = selectedTab == tab

                        if (tab == MainTab.EventViewer) {
                            if (isPortalVisible) {
                                WideNavigationRailItem(
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
                                            AsyncImage(
                                                model = "https://xn--d1ah4a.com/assets/portal/portal-active.gif",
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(28.dp)
                                            )
                                        }
                                    },
                                    label =  labeled(
                                        label = stringResource(tab.label)
                                    ),
                                    railExpanded = railState.currentValue == WideNavigationRailValue.Expanded,
                                )
                            }
                        } else {
                            WideNavigationRailItem(
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
                                ),
                                railExpanded = railState.currentValue == WideNavigationRailValue.Expanded,
                            )
                        }

                    }
                }
            }

        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TabUIStatelessPreview() {
    AppTheme(
        navFloating = false
    ) {
        TabUIStateless(
            hideNav = false,
            selectedTab = MainTab.Feed,
            notificationCount = 3,
            onNavItemClick = {},
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Content")
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TabUIStatelessPreview2() {
    AppTheme(
        navFloating = true
    ) {
        TabUIStateless(
            hideNav = false,
            selectedTab = MainTab.Feed,
            notificationCount = 3,
            onNavItemClick = {},
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Content")
                }
            }
        )
    }
}

