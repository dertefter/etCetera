package com.dertefter.etcetera.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.dertefter.comments.CommentsRoute
import com.dertefter.etcetera.navigation.AppNavHost
import com.dertefter.navigation.NavigationAction
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.new_post.NewPostRoute
import com.gigamole.composefadingedges.FadingEdgesGravity
import com.gigamole.composefadingedges.fill.FadingEdgesFillType
import com.gigamole.composefadingedges.verticalFadingEdges

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigator: Navigator,
    mainScreenState: MainScreenState
){

    val navController = rememberNavController()

    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var bottomSheetRoute by remember { mutableStateOf<Routes?>(null) }
    val sheetState = rememberBottomSheetState( SheetValue.Hidden)


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

    val startDestination = if (mainScreenState.isAuthorized) Routes.Feed else Routes.Auth

    AppNavHost(
        navController = navController,
        modifier = Modifier
            .verticalFadingEdges(
                fillType = FadingEdgesFillType.FadeColor(color = MaterialTheme.colorScheme.background),
                gravity = FadingEdgesGravity.End,
                length = navigationBarHeight + (navigationBarHeight * 0.5f )
            )
            .fillMaxSize(),
        startDestination = startDestination
    )
}
