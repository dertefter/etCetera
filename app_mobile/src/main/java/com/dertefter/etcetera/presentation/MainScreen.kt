package com.dertefter.etcetera.presentation

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.stringResource
import com.dertefter.comments.CommentsRoute
import com.dertefter.data.common.AppError
import com.dertefter.etcetera.R
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.isFold
import com.dertefter.design.theme.spacing
import com.dertefter.navigation.NavigationAction
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.new_post.NewCommentReplyRoute
import com.dertefter.new_post.NewCommentRoute
import com.dertefter.new_post.NewPostRoute
import com.dertefter.new_post.RepostRoute
import com.dertefter.new_post.EditPostRoute
import dev.chrisbanes.haze.blur.HazeBlurDefaults
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

enum class MainTab(
    val label: String,
    val icon: @Composable () -> ImageVector,
    val selectedIcon: @Composable () -> ImageVector
) {
    Feed("Лента", { Icons.Home }, { Icons.HomeFilled }),

    Search("Поиск", { Icons.Search }, { Icons.Search }),
    Notifications("Уведомления", { Icons.Notifications }, { Icons.NotificationsFilled }),
    Profile("Профиль", { Icons.User }, { Icons.UserFilled })
}

@Composable
fun getErrorMessage(e: AppError?): String? {
    return when (e) {
        null -> null
        is AppError.Network -> stringResource(R.string.app_error_network)
        is AppError.TurnstileVerificationFailed -> stringResource(R.string.app_error_turnstile)
        is AppError.ApiError -> e.message ?: stringResource(R.string.app_error_api_with_code, e.code ?: "")
        is AppError.Unexpected -> e.message ?: stringResource(R.string.app_error_unexpected)

        // Authentication & Authorization
        is AppError.Unauthorized -> stringResource(R.string.app_error_unauthorized)
        is AppError.SessionNotFound -> stringResource(R.string.app_error_session_not_found)
        is AppError.SessionExpired -> stringResource(R.string.app_error_session_expired)
        is AppError.SessionRevoked -> stringResource(R.string.app_error_session_revoked)
        is AppError.RefreshTokenMissing -> stringResource(R.string.app_error_refresh_token_missing)
        is AppError.InvalidCredentials -> stringResource(R.string.app_error_invalid_credentials)
        is AppError.AccountBanned -> stringResource(R.string.app_error_account_banned)
        is AppError.AccountDeleted -> stringResource(R.string.app_error_account_deleted)
        is AppError.ProfileRequired -> stringResource(R.string.app_error_profile_required)
        is AppError.EmailDomainNotAllowed -> stringResource(R.string.app_error_email_domain_not_allowed)

        // Validation & Constraints
        is AppError.ValidationError -> e.message ?: stringResource(R.string.app_error_validation)
        is AppError.UsernameTaken -> stringResource(R.string.app_error_username_taken)
        is AppError.InvalidDisplayName -> stringResource(R.string.app_error_invalid_display_name)
        is AppError.SamePassword -> stringResource(R.string.app_error_same_password)
        is AppError.InvalidOldPassword -> stringResource(R.string.app_error_invalid_old_password)
        is AppError.InvalidPassword -> stringResource(R.string.app_error_invalid_password)
        is AppError.BannedWord -> stringResource(R.string.app_error_banned_word)

        // Resource Status
        is AppError.NotFound -> stringResource(R.string.app_error_not_found)
        is AppError.AlreadyDeleted -> stringResource(R.string.app_error_already_deleted)
        is AppError.Conflict -> e.message ?: stringResource(R.string.app_error_conflict)
        is AppError.UserBlocked -> stringResource(R.string.app_error_user_blocked)
        is AppError.NotPinned -> stringResource(R.string.app_error_not_pinned)

        // Permissions & Access
        is AppError.Forbidden -> stringResource(R.string.app_error_forbidden)
        is AppError.PinNotOwned -> stringResource(R.string.app_error_pin_not_owned)
        is AppError.RequiresVerification -> stringResource(R.string.app_error_requires_verification)
        is AppError.RequiresSubscription -> stringResource(R.string.app_error_requires_subscription)

        // Operations
        is AppError.RateLimit -> stringResource(R.string.app_error_rate_limit)
        is AppError.UploadError -> stringResource(R.string.app_error_upload)
        is AppError.ModerationError -> stringResource(R.string.app_error_moderation)
        is AppError.EditWindowExpired -> stringResource(R.string.app_error_edit_window_expired)
        is AppError.NotDeleted -> stringResource(R.string.app_error_not_deleted)
        is AppError.Internal -> stringResource(R.string.app_error_internal)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigator: Navigator,
    currentLogin: String? = null,
    meUserId: String? = null,
    currentError: AppError? = null,
) {
    val authBackStack = rememberNavBackStack(Routes.Auth)
    val feedBackStack = rememberNavBackStack(Routes.Feed)
    val searchBackStack = rememberNavBackStack(Routes.Search)
    val notificationsBackStack = rememberNavBackStack(Routes.Notifications(showBackButton = false))
    val profileBackStack = rememberNavBackStack(
        if (meUserId != null) Routes.User(meUserId) else Routes.Auth
    )

    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Feed) }

    val activeBackStack = when {
        currentLogin == null -> authBackStack
        selectedTab == MainTab.Feed -> feedBackStack
        selectedTab == MainTab.Search -> searchBackStack
        selectedTab == MainTab.Notifications -> notificationsBackStack
        selectedTab == MainTab.Profile -> profileBackStack
        else -> feedBackStack
    }

    LaunchedEffect(currentLogin) {
        feedBackStack.clear()
        feedBackStack.add(Routes.Feed)
        searchBackStack.clear()
        searchBackStack.add(Routes.Search)
        notificationsBackStack.clear()
        notificationsBackStack.add(Routes.Notifications(showBackButton = false))
        if (currentLogin == null) {
            authBackStack.clear()
            authBackStack.add(Routes.Auth)
            profileBackStack.clear()
            profileBackStack.add(Routes.Auth)
        }
    }

    LaunchedEffect(meUserId) {
        if (meUserId != null && currentLogin != null) {
            val lastRoute = profileBackStack.lastOrNull()
            if (lastRoute !is Routes.User || lastRoute.userId != meUserId) {
                profileBackStack.clear()
                profileBackStack.add(Routes.User(meUserId, showBackButton = false))
            }
        }
    }



    var bottomSheetRoute by remember { mutableStateOf<Routes?>(null) }
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = SheetValue.Hidden
        )
    )

    val hazeState = rememberHazeState()

    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    val errorMessage = getErrorMessage(currentError)

    LaunchedEffect(errorMessage) {
        scope.launch {
            errorMessage?.let{ errorMessage ->
                snackbarHostState
                    .showSnackbar(
                        message = errorMessage,
                        duration = SnackbarDuration.Short
                    )
            }
        }
    }

    BackHandler(
        enabled = selectedTab != MainTab.Feed
    ) {
       scope.launch {
           selectedTab = MainTab.Feed
       }
    }

    BackHandler(
        enabled = scaffoldState.bottomSheetState.currentValue != SheetValue.Hidden
    ) {
        scope.launch {
            scaffoldState.bottomSheetState.hide()
        }
    }

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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
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
                    is Routes.NewPost -> NewPostRoute(route.wallRecipientId)
    is Routes.Repost -> RepostRoute(route.postIdForRepost, route.wallRecipientId)
    is Routes.EditPost -> EditPostRoute(route.postId)
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
            navigator = navigator,
        )
    }
}
