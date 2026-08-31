package com.dertefter.etcetera.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.dertefter.design.theme.isTab
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing
import com.dertefter.navigation.Routes
import com.gigamole.composefadingedges.FadingEdgesGravity
import com.gigamole.composefadingedges.fill.FadingEdgesFillType
import com.gigamole.composefadingedges.verticalFadingEdges
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun TabUI(
    modifier: Modifier = Modifier,
    activeBackStack: NavBackStack<NavKey>,
    entries: List<NavEntry<NavKey>>,
    selectedTab: MainTab,
    notificationCount: Int?,
    hazeState: HazeState,
    appNavHost: @Composable (
        entries: List<NavEntry<NavKey>>,
        onBack: () -> Unit,
        modifier: Modifier
    ) -> Unit,
    onBack: () -> Unit,
    onNavItemClick: (tab: MainTab) -> Unit
) {
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val hideNav = activeBackStack.lastOrNull() is Routes.AttachmentsViewer || activeBackStack.lastOrNull() is Routes.Auth

    val topLeftCornerRadius = MaterialTheme.rounding.largeIncreased

    TabUIStateless(
        modifier = modifier,
        hideNav = hideNav,
        selectedTab = selectedTab,
        notificationCount = notificationCount,
        hazeState = hazeState,
        onNavItemClick = onNavItemClick,
        content = {
            appNavHost(
                entries,
                onBack,
                Modifier
                    .then(
                        if (!hideNav) {
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
                    .then(
                        if (!hideNav) {
                            Modifier
                                .statusBarsPadding()
                                .clip(RoundedCornerShape(topStart = topLeftCornerRadius))
                        } else Modifier
                    )
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
                                    contentDescription = tab.label
                                )
                            }
                        },
                        label = {
                            Text(
                                tab.label,
                                style = if (railState.currentValue == WideNavigationRailValue.Expanded) {
                                    MaterialTheme.typography.labelLarge
                                } else {
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
