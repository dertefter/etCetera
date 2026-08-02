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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.rememberNavBackStack
import com.dertefter.comments.CommentsRoute
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.isFold
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
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

enum class MainTab(
    val label: String,
    val icon: @Composable () -> ImageVector,
    val selectedIcon: @Composable () -> ImageVector
) {
    Feed("Лента", { Icons.Home }, { Icons.HomeFilled }),
    Notifications("Уведомления", { Icons.Notifications }, { Icons.NotificationsFilled }),
    Profile("Профиль", { Icons.User }, { Icons.UserFilled })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigator: Navigator,
    currentLogin: String? = null,
    meUserId: String? = null,
) {

    val authBackStack = rememberNavBackStack(Routes.Auth)
    val feedBackStack = rememberNavBackStack(Routes.Feed)
    val notificationsBackStack = rememberNavBackStack(Routes.Notifications(showBackButton = false))
    val profileBackStack = rememberNavBackStack(
        if (meUserId != null) Routes.User(meUserId) else Routes.Feed
    )

    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Feed) }

    val activeBackStack = when {
        currentLogin == null -> authBackStack
        selectedTab == MainTab.Feed -> feedBackStack
        selectedTab == MainTab.Notifications -> notificationsBackStack
        selectedTab == MainTab.Profile -> profileBackStack
        else -> feedBackStack
    }

    LaunchedEffect(currentLogin) {
        if (currentLogin == null) {
            authBackStack.clear()
            authBackStack.add(Routes.Auth)
            feedBackStack.clear()
            feedBackStack.add(Routes.Feed)
            notificationsBackStack.clear()
            notificationsBackStack.add(Routes.Notifications(showBackButton = false))
            profileBackStack.clear()
            profileBackStack.add(Routes.Auth)
        }
    }

    LaunchedEffect(meUserId) {
        if (meUserId != null && currentLogin != null && profileBackStack.lastOrNull() !is Routes.User) {
            profileBackStack.clear()
            profileBackStack.add(Routes.User(meUserId, showBackButton = false))
        }
    }

    val isAttachmentViewer = activeBackStack.lastOrNull() is Routes.AttachmentsViewer

    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var bottomSheetRoute by remember { mutableStateOf<Routes?>(null) }
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = SheetValue.Hidden
        )
    )

    val hazeState = rememberHazeState()

    val scope = rememberCoroutineScope()

//    BackHandler(enabled = scaffoldState.bottomSheetState.currentValue != SheetValue.Hidden) {
//        scope.launch {
//            scaffoldState.bottomSheetState.hide()
//        }
//    }

    val blurRadius by animateDpAsState(
        targetValue = if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) 120.dp else 60.dp,
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
            ) {
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
            if (MaterialTheme.isFold){
                TabUI(
                    activeBackStack = activeBackStack,
                    currentLogin = currentLogin,
                    selectedTab = selectedTab,
                    hazeState = hazeState,
                    onBack = {
                        if (activeBackStack.size > 1) {
                            activeBackStack.removeAt(activeBackStack.lastIndex)
                        } else if (currentLogin != null && selectedTab != MainTab.Feed) {
                            selectedTab = MainTab.Feed
                        }
                    },
                    onNavItemClick = { tab ->
                        selectedTab = tab
                    }
                )
            } else{
                PhoneUI(
                    activeBackStack = activeBackStack,
                    currentLogin = currentLogin,
                    selectedTab = selectedTab,
                    hazeState = hazeState,
                    hazeStyle = hazeStyle,
                    onBack = {
                        if (activeBackStack.size > 1) {
                            activeBackStack.removeAt(activeBackStack.lastIndex)
                        } else if (currentLogin != null && selectedTab != MainTab.Feed) {
                            selectedTab = MainTab.Feed
                        }
                    },
                    onNavItemClick = { tab ->
                        selectedTab = tab
                    }
                )
            }

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

    val currentActiveBackStack by rememberUpdatedState(activeBackStack)
    LaunchedEffect(navigator) {
        navigator.navigationActions.collect { action ->
            val backStack = currentActiveBackStack
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

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navigator = object : Navigator {
        override val navigationActions = emptyFlow<NavigationAction>()
        override fun navigate(route: Routes) {}
        override fun openAsBottomSheet(route: Routes) {}
        override fun hideBottomSheet() {}
        override fun navigateUp() {}
        override fun navigateAndClearBackStack(route: Routes, popupTo: Routes, inclusive: Boolean) {}
    }
    AppTheme {
        MainScreen(
            navigator = navigator
        )
    }
}
