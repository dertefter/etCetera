package com.dertefter.etcetera.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.rememberNavBackStack
import com.dertefter.comments.CommentsRoute
import com.dertefter.design.theme.spacing
import com.dertefter.etcetera.navigation.AppNavHost
import com.dertefter.navigation.NavigationAction
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.new_comment.NewCommentReplyRoute
import com.dertefter.new_comment.NewCommentRoute
import com.dertefter.new_post.NewPostRoute
import com.gigamole.composefadingedges.FadingEdgesGravity
import com.gigamole.composefadingedges.fill.FadingEdgesFillType
import com.gigamole.composefadingedges.verticalFadingEdges
import dev.chrisbanes.haze.blur.HazeBlurDefaults
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigator: Navigator,
    mainScreenState: MainScreenState
){

    val startDestination = if (mainScreenState.isAuthorized) Routes.Feed else Routes.Auth
    val backStack = rememberNavBackStack(startDestination)

    LaunchedEffect(mainScreenState.isAuthorized) {
        if (mainScreenState.isAuthorized) {
            if (backStack.contains(Routes.Auth)) {
                backStack.clear()
                backStack.add(Routes.Feed)
            }
        } else {
            if (!backStack.contains(Routes.Auth)) {
                backStack.clear()
                backStack.add(Routes.Auth)
            }
        }
    }

    val isAttachmentViewer = backStack.lastOrNull() is Routes.AttachmentsViewer

    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var bottomSheetRoute by remember { mutableStateOf<Routes?>(null) }
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = SheetValue.Hidden
        )
    )

    val hazeState = rememberHazeState()

    val scope = rememberCoroutineScope()

    BackHandler(enabled = scaffoldState.bottomSheetState.currentValue != SheetValue.Hidden) {
        scope.launch {
            scaffoldState.bottomSheetState.hide()
        }
    }

    val blurRadius by animateDpAsState(
        targetValue = if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded)  80.dp else 40.dp,
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
    )

    val hazeStyle = HazeBlurDefaults.style(
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
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
        modifier = Modifier.imePadding(),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 400.dp,
        sheetContainerColor = Color.Transparent,
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        sheetDragHandle = null,
        sheetShadowElevation = 0.dp,
        sheetContent = {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxSize()
                    .clip(BottomSheetDefaults.ExpandedShape)
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
                    is Routes.NewPost -> NewPostRoute(route.wallRecipientId, route.postIdForRepost)
                    is Routes.NewComment -> NewCommentRoute(route.postId)
                    is Routes.NewCommentReply -> NewCommentReplyRoute(route.postId, route.commentId, route.userId)
                    else -> {
                        Spacer(Modifier.height(1.dp))
                    }
                }
            }
        }
    ) {
        Box(Modifier.fillMaxSize()) {
            AppNavHost(
                backStack = backStack,
                onBack = {
                    if (backStack.size > 1) {
                        backStack.removeAt(backStack.lastIndex)
                    }
                },
                modifier = Modifier
                    .hazeSource(hazeState)
                    .then(
                        if (!isAttachmentViewer) {
                            Modifier.verticalFadingEdges(
                                fillType = FadingEdgesFillType.FadeColor(
                                    color = MaterialTheme.colorScheme.background

                                ),
                                gravity = FadingEdgesGravity.End,
                                length = navigationBarHeight + 12.dp
                            )
                        } else Modifier
                    )
                    .fillMaxSize()
            )

            if (scaffoldState.bottomSheetState.currentValue != SheetValue.Hidden) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            scope.launch {
                                scaffoldState.bottomSheetState.hide()
                            }
                        }
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        navigator.navigationActions.collect { action ->
            when (action) {
                is NavigationAction.Navigate -> {
                    scaffoldState.bottomSheetState.hide()
                    backStack.add(action.route)
                }

                is NavigationAction.NavigateAndClearBackStack -> {
                    val index = backStack.indexOfLast { it == action.popupTo }
                    if (index != -1) {
                        val removeIndex = if (action.inclusive) index else index + 1
                        while (backStack.size > removeIndex) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    }
                    backStack.add(action.route)
                }

                NavigationAction.NavigateUp -> {
                    if (backStack.size > 1) {
                        backStack.removeAt(backStack.lastIndex)
                    }
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
