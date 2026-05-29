package com.dertefter.etcetera.presentation.adaptive

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.dertefter.etcetera.navigation.AppNavHost
import com.dertefter.etcetera.navigation.TabRouteItem
import com.dertefter.etcetera.presentation.components.MainBottomBar
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhoneUi(
    navController: NavHostController,
    currentDestination: NavDestination?,
    navigationItems: List<TabRouteItem> = emptyList(),
    isFullScreen: Boolean
){

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = !isFullScreen,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ){
                MainBottomBar(
                    navController = navController,
                    currentDestination = currentDestination,
                    navigationItems = navigationItems,
                )
            }

        }
    ) { paddingValues ->
        val bottomPadding by animateDpAsState(
            if (isFullScreen) 0.dp else paddingValues.calculateBottomPadding()
        )

        Box(
            modifier = Modifier
                .consumeWindowInsets(WindowInsets(bottom = bottomPadding))
                .padding(bottom = bottomPadding)
                .imePadding()
                .fillMaxSize()
        ) {
            AppNavHost(
                navController = navController,
                navItems = navigationItems
            )

        }
    }


}
