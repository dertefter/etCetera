package com.dertefter.etcetera.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dertefter.etcetera.navigation.AppNavHost
import com.dertefter.navigation.Routes
import com.gigamole.composefadingedges.FadingEdgesGravity
import com.gigamole.composefadingedges.fill.FadingEdgesFillType
import com.gigamole.composefadingedges.verticalFadingEdges
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun PhoneUI(
    modifier: Modifier = Modifier,
    activeBackStack: NavBackStack<NavKey>,
    selectedTab: MainTab,
    hazeState: HazeState,
    onBack: () -> Unit,
    onNavItemClick: (tab: MainTab) -> Unit


    ){

    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val showFadingEdges = activeBackStack.lastOrNull() is Routes.AttachmentsViewer

    val hideNav = activeBackStack.lastOrNull() is Routes.AttachmentsViewer || activeBackStack.lastOrNull() is Routes.Auth

    Column(modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            AppNavHost(
                backStack = activeBackStack,
                onBack = onBack,
                modifier = Modifier
                    .hazeSource(hazeState)
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
                    .consumeWindowInsets( WindowInsets.navigationBars.asPaddingValues())
                    .fillMaxSize()
            )
        }

        if (!hideNav) {
            NavigationBar(
                containerColor = Color.Transparent,
            ) {
                MainTab.entries.forEach { tab ->
                    val selected = selectedTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = {onNavItemClick(tab)},
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon() else tab.icon(),
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    }
}