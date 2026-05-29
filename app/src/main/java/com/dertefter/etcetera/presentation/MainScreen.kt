package com.dertefter.etcetera.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dertefter.comments.CommentsRoute
import com.dertefter.design.theme.cornerShape
import com.dertefter.design.theme.isTab
import com.dertefter.etcetera.navigation.getNavigationMenu
import com.dertefter.etcetera.presentation.adaptive.PhoneUi
import com.dertefter.etcetera.presentation.adaptive.TabUI
import com.dertefter.navigation.NavigationAction
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.new_post.NewPostRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigator: Navigator,
    mainScreenState: MainScreenState
){

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var bottomSheetRoute by remember { mutableStateOf<Routes?>(null) }
    val sheetState = rememberBottomSheetState( SheetValue.Hidden)

    val isFullScreen = false

    val navigationItems = getNavigationMenu(mainScreenState.isAuthorized)

    if (bottomSheetRoute != null) {
        ModalBottomSheet(
            onDismissRequest = { bottomSheetRoute = null },
            sheetState = sheetState,
            contentWindowInsets = { WindowInsets.statusBars }
        ) {
            when (val route = bottomSheetRoute) {
                is Routes.Comments -> CommentsRoute(route.postId)
                is Routes.NewPost -> NewPostRoute(route.wallRecipientId)
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        navigator.navigationActions.collect { action ->
            when (action) {
                is NavigationAction.Navigate -> {
                    sheetState.hide()
                    navController.navigate(action.route)
                }

                is NavigationAction.NavigateAndClearBackStack -> {
                    navController.navigate(action.route) {
                        popUpTo(action.popupTo) {
                            inclusive = action.inclusive
                        }
                    }
                }

                NavigationAction.NavigateUp -> {
                    navController.navigateUp()
                }

                is NavigationAction.OpenAsBottomSheet -> {
                    bottomSheetRoute = action.route
                }

                is NavigationAction.HideBottomSheet -> {
                    sheetState.hide()
                    bottomSheetRoute = null
                }

            }
        }
    }


    if (MaterialTheme.isTab) {
        TabUI(
            navController = navController,
            currentDestination = currentDestination,
            navigationItems = navigationItems,
            isFullScreen = isFullScreen
        )
    } else {
        PhoneUi(
            navController = navController,
            currentDestination = currentDestination,
            navigationItems = navigationItems,
            isFullScreen = isFullScreen
        )
    }
}