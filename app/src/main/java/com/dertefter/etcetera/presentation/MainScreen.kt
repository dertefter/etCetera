package com.dertefter.etcetera.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dertefter.comments.CommentsRoute
import com.dertefter.design.theme.spacing
import com.dertefter.etcetera.navigation.AppNavHost
import com.dertefter.navigation.NavigationAction
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.new_post.NewPostRoute
import com.gigamole.composefadingedges.FadingEdgesGravity
import com.gigamole.composefadingedges.fill.FadingEdgesFillType
import com.gigamole.composefadingedges.verticalFadingEdges
import dev.chrisbanes.haze.blur.HazeBlurDefaults
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigator: Navigator,
    mainScreenState: MainScreenState
){

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isAttachmentViewer = navBackStackEntry?.destination?.hasRoute<Routes.AttachmentsViewer>() ?: false

    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var bottomSheetRoute by remember { mutableStateOf<Routes?>(null) }
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = SheetValue.Hidden
        )
    )

    val hazeState = rememberHazeState()

    val blurRadius by animateDpAsState(
        targetValue = if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded)  40.dp else 26.dp
    )

    val hazeStyle = HazeBlurDefaults.style(
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        blurRadius = blurRadius,
        noiseFactor = 0.6f,
    )

    LaunchedEffect(bottomSheetRoute) {
        if (bottomSheetRoute != null) {
            scaffoldState.bottomSheetState.partialExpand()
        } else {
            scaffoldState.bottomSheetState.hide()
        }
    }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden) {
            bottomSheetRoute = null
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 500.dp,
        sheetContainerColor = Color.Transparent,
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        sheetDragHandle = null,
        sheetShadowElevation = 0.dp,
        sheetContent = {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.extraLarge)
                    .hazeEffect(state = hazeState) {
                        blurEffect {
                            style = hazeStyle
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ){
                BottomSheetDefaults.DragHandle()
                when (val route = bottomSheetRoute) {
                    is Routes.Comments -> CommentsRoute(route.postId)
                    is Routes.NewPost -> NewPostRoute(route.wallRecipientId)
                    else -> {
                        Spacer(Modifier.height(1.dp))
                    }
                }
            }
        }
    ) {
        val startDestination = if (mainScreenState.isAuthorized) Routes.Feed else Routes.Auth

        AppNavHost(
            navController = navController,
            modifier = Modifier
                .hazeSource(hazeState)
                .then(
                    if (!isAttachmentViewer) {
                        Modifier.verticalFadingEdges(
                            fillType = FadingEdgesFillType.FadeColor(color = MaterialTheme.colorScheme.background),
                            gravity = FadingEdgesGravity.End,
                            length = navigationBarHeight + (navigationBarHeight * 0.5f)
                        )
                    } else Modifier
                )
                .fillMaxSize(),
            startDestination = startDestination
        )
    }

    LaunchedEffect(Unit) {
        navigator.navigationActions.collect { action ->
            when (action) {
                is NavigationAction.Navigate -> {
                    scaffoldState.bottomSheetState.hide()
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
                    scaffoldState.bottomSheetState.hide()
                    bottomSheetRoute = null
                }

            }
        }
    }
}
