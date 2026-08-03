package com.dertefter.etcetera.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailColors
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailItemDefaults
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.isTab
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing
import com.dertefter.etcetera.navigation.AppNavHost
import com.dertefter.navigation.Routes
import com.gigamole.composefadingedges.FadingEdgesGravity
import com.gigamole.composefadingedges.fill.FadingEdgesFillType
import com.gigamole.composefadingedges.verticalFadingEdges
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurDefaults
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun TabUI(
    modifier: Modifier = Modifier,
    activeBackStack: NavBackStack<NavKey>,
    currentLogin: String?,
    selectedTab: MainTab,
    hazeState: HazeState,
    onBack: () -> Unit,
    onNavItemClick: (tab: MainTab) -> Unit
) {
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val showFadingEdges = activeBackStack.lastOrNull() !is Routes.AttachmentsViewer

    val hideNav = activeBackStack.lastOrNull() is Routes.AttachmentsViewer || activeBackStack.lastOrNull() is Routes.Auth

    val topLeftCornerRadius by animateDpAsState(
        if (hideNav) 0.dp else MaterialTheme.rounding.largeIncreased
    )

    TabUIStateless(
        modifier = modifier,
        hideNav = hideNav,
        selectedTab = selectedTab,
        hazeState = hazeState,
        onNavItemClick = onNavItemClick,
        content = {
            AppNavHost(
                backStack = activeBackStack,
                onBack = onBack,
                modifier = Modifier
                    .then(
                        if (showFadingEdges) {
                            Modifier.verticalFadingEdges(
                                fillType = FadingEdgesFillType.FadeColor(
                                    color = MaterialTheme.colorScheme.background
                                ),
                                gravity = FadingEdgesGravity.End,
                                length = navigationBarHeight + 12.dp
                            )
                        } else Modifier
                    )
                    .clipToBounds()
                    .statusBarsPadding()
                    .clip(RoundedCornerShape(topStart = topLeftCornerRadius))
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
    hazeState: HazeState,
    onNavItemClick: (tab: MainTab) -> Unit,
    content: @Composable () -> Unit
) {
    val isTab = MaterialTheme.isTab
    val railState = rememberWideNavigationRailState(
        initialValue = if (isTab) WideNavigationRailValue.Expanded else WideNavigationRailValue.Collapsed
    )

    LaunchedEffect(isTab) {
        if (isTab) {
            railState.expand()
        } else {
            railState.collapse()
        }
    }

    Row(
        modifier
            .hazeSource(hazeState)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .fillMaxSize()
    ) {
        if (!hideNav) {
            WideNavigationRail(
                modifier = Modifier,
                state = railState,
                colors = WideNavigationRailDefaults.colors(
                    containerColor = Color.Transparent
                ),
                arrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)

            ) {
                MainTab.entries.forEach { tab ->

                    val selected = selectedTab == tab

                    WideNavigationRailItem(
                        selected = selected,
                        onClick = { onNavItemClick(tab) },
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon() else tab.icon(),
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                tab.label,
                                style = if (railState.currentValue == WideNavigationRailValue.Expanded) {
                                    MaterialTheme.typography.labelLarge
                                }else {
                                    MaterialTheme.typography.labelMedium
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        railExpanded = railState.currentValue == WideNavigationRailValue.Expanded,
                    )
                }
            }
        }

        Box(Modifier.weight(1f)) {
            content()
        }
    }
}