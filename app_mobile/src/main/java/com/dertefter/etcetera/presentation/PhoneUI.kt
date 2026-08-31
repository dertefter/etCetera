package com.dertefter.etcetera.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.dertefter.navigation.Routes
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhoneUI(
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

    val hideNav = activeBackStack.lastOrNull() is Routes.AttachmentsViewer || activeBackStack.lastOrNull() is Routes.Auth || WindowInsets.isImeVisible

    val consumedPaddingValues = if (hideNav) PaddingValues(0.dp) else WindowInsets.navigationBars.asPaddingValues()

    Column(modifier.fillMaxSize()) {
        appNavHost(
            entries,
            onBack,
            Modifier
                .hazeSource(hazeState)
                .consumeWindowInsets(consumedPaddingValues)
                .weight(1f)
                .fillMaxSize()
        )
        AnimatedVisibility(
            visible = !hideNav
        ) {
            NavigationBar {
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
                                    contentDescription = tab.label
                                )
                            }
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }

    }
}
