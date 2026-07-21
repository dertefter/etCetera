package com.dertefter.etcetera.presentation

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing
import com.dertefter.etcetera.navigation.AppNavHost
import com.dertefter.navigation.Routes
import com.gigamole.composefadingedges.FadingEdgesGravity
import com.gigamole.composefadingedges.fill.FadingEdgesFillType
import com.gigamole.composefadingedges.verticalFadingEdges
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource

@Composable
fun FoldUI(
    modifier: Modifier = Modifier,
    activeBackStack: NavBackStack<NavKey>,
    currentLogin: String?,
    selectedTab: MainTab,
    hazeState: HazeState,
    hazeStyle: HazeBlurStyle,
    onBack: () -> Unit,
    onNavItemClick: (tab: MainTab) -> Unit


) {

    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val showFadingEdges = activeBackStack.lastOrNull() !is Routes.AttachmentsViewer

    val showNav =
        activeBackStack.lastOrNull() !is Routes.AttachmentsViewer || activeBackStack.lastOrNull() !is Routes.Auth || currentLogin != null

    Row(
        modifier
            .hazeSource(hazeState)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .fillMaxSize()
    ) {

        if (showNav) {
            NavigationRail(
                modifier = Modifier,
                containerColor = Color.Transparent
            ) {
                MainTab.entries.forEach { tab ->

                    val selected = selectedTab == tab

                    NavigationRailItem(
                        selected = selected,
                        onClick = { onNavItemClick(tab) },
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon() else tab.icon(),
                                contentDescription = tab.label
                            )
                        },
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium),
                        label = { Text(tab.label) }
                    )
                }
            }
        }

        Box(Modifier.weight(1f)) {
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
                    .clip(RoundedCornerShape(topStart = MaterialTheme.rounding.largeIncreased, ))
                    .fillMaxSize()
            )
        }


    }
}