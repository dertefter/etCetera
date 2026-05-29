package com.dertefter.etcetera.presentation.adaptive

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.dertefter.etcetera.navigation.AppNavHost
import com.dertefter.etcetera.navigation.TabRouteItem
import com.dertefter.etcetera.presentation.components.MainNavigationRail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabUI(
    navController: NavHostController,
    currentDestination: NavDestination?,
    navigationItems: List<TabRouteItem> = emptyList(),
    navHost: @Composable (Modifier) -> Unit = { modifier ->
        AppNavHost(
            modifier = modifier,
            navController = navController,
            navigationItems
        )
    },
    isFullScreen: Boolean
){
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(modifier = Modifier
                .imePadding()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .fillMaxSize())
            {
                AnimatedVisibility(
                    visible = !isFullScreen
                ) {
                    MainNavigationRail(
                        navController = navController,
                        currentDestination = currentDestination,
                        navigationItems = navigationItems,
                    )
                }

                val leftPadding by animateDpAsState(
                    if (isFullScreen) 0.dp else paddingValues.calculateLeftPadding(LocalLayoutDirection.current)
                )

                navHost(
                    Modifier
                        .consumeWindowInsets(
                            WindowInsets(
                                left = leftPadding,
                            )
                        )
                        .weight(1f)
                        .fillMaxSize()
                )
            }
        }
    }

}