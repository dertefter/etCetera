package com.dertefter.user.presentation

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.data.common.Constants
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.user.UserDto
import com.dertefter.data.dto.user.VisibilityDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.avatar.EmojiAvatar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.common.ErrorLarge
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.isFold
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing
import com.dertefter.user.R
import com.dertefter.user.presentation.component.BioCard
import com.dertefter.user.presentation.component.Header
import com.dertefter.user.presentation.component.HeaderWide
import com.dertefter.user.presentation.mapper.toUiModel
import com.jamal_aliev.paginator.compose.cursor.rememberPaginated
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun UserScreen(
    onEvent: (Event) -> Unit,
    userUiState: UserUiState,
    selectedTab: FeedTab,
    uiStates: Map<FeedTab, PaginatorUiState<PostDto>>,
    paginators: Map<FeedTab, MutableCursorPaginator<String, PostDto>> = emptyMap(),
    showBackButton: Boolean,
) {

    val context = LocalContext.current

    val lazyListState = rememberLazyListState()

    val scope = rememberCoroutineScope()

    var showAccountSelector by remember { mutableStateOf(false) }

    if (showAccountSelector) {
        ModalBottomSheet(
            onDismissRequest = { showAccountSelector = false },
            sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.large),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Text(
                    text = stringResource(R.string.user_select_account),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.small)
                )
                userUiState.loginHistory.forEach { login ->
                    val isCurrent = login == userUiState.currentLogin
                    ListItem(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                if (!isCurrent) {
                                    onEvent(Event.OnSwitchAccount(login))
                                }
                                showAccountSelector = false
                            },
                        leadingContent = {
                            Icon(
                                if (isCurrent) Icons.Check else Icons.User,
                                contentDescription = null,
                                tint = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingContent = {
                            if (!isCurrent) {
                                AppNavigationIcon(
                                    icon = Icons.Delete,
                                    onClick = { onEvent(Event.OnRemoveAccountFromHistory(login)) }
                                )
                            }
                        },
                        overlineContent = null,
                        supportingContent = null,
                        colors = ListItemDefaults.colors(
                            containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.4f
                            ) else Color.Transparent
                        ),
                        content = {
                            Text(
                                text = login,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                    )
                }

                Button(
                    onClick = {
                        onEvent(Event.OnAddAccount)
                        showAccountSelector = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Add, contentDescription = null)
                    Spacer(Modifier.width(MaterialTheme.spacing.small))
                    Text(stringResource(R.string.user_add_account))
                }
            }
        }
    }

    val isUpFabVisible by remember(lazyListState) {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 6
        }
    }

    val addFabSize by animateDpAsState(
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        targetValue = if (isUpFabVisible) 40.dp else 64.dp,
        label = "addFabSize"
    )

    val addFabCornerRadius by animateDpAsState(
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        targetValue = if (isUpFabVisible) MaterialTheme.rounding.medium else MaterialTheme.rounding.largeIncreased,
        label = "addFabCornerRadius"
    )

    val isStickyHeaderStuck by remember(lazyListState) {
        derivedStateOf {
            val headerItem = lazyListState.layoutInfo.visibleItemsInfo.find { it.key == "tabs" }
            if (headerItem != null) {
                lazyListState.firstVisibleItemIndex > headerItem.index ||
                        (lazyListState.firstVisibleItemIndex == headerItem.index && lazyListState.firstVisibleItemScrollOffset > 0)
            } else {
                false
            }
        }
    }


    val isNewPostButtonShow by remember(userUiState) {
        derivedStateOf {
            val user = userUiState.userDto
            if (user != null) {
                userUiState.isMe || when (user.wallAccess) {
                    VisibilityDto.EVERYONE -> true
                    VisibilityDto.FOLLOWERS -> user.isFollowedBy
                    VisibilityDto.MUTUAL -> user.isFollowedBy && user.isFollowing
                    VisibilityDto.NOBODY -> false
                }
            } else {
                false
            }
        }
    }

    val tabs = FeedTab.entries

    val paginator = paginators[selectedTab]
    val paged = paginator?.rememberPaginated(state = lazyListState)

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val isScrolled by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0 || lazyListState.firstVisibleItemScrollOffset > 0
        }
    }

    val appBarTitleAlpha by animateFloatAsState(
        targetValue = if (isScrolled) 1f else 0f,
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        label = "appBarTitleAlpha"
    )

    val appBarContainerColor by animateColorAsState(
        targetValue = if (isScrolled) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface,
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        label = "appBarContainerColor"
    )

    val shareText = userUiState.userDto?.let {
        stringResource(R.string.user_share_profile_url, it.username)
    } ?: ""

    val currentTabUiState = uiStates[selectedTab]
    val isRefreshing = userUiState.isLoading
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(lazyListState) {
        delay(Constants.STATS_UPDATE_DELAY_MS.milliseconds)
        while (true) {
            val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
            val visibleIds = visibleItems.mapNotNull {
                it.key.toString().takeIf { it.startsWith("post_") }?.removePrefix("post_")
            }
            if (visibleIds.isNotEmpty()) {
                onEvent(Event.OnUpdateStats(visibleIds))
            }
            delay(Constants.STATS_UPDATE_DELAY_MS.milliseconds)
        }
    }

    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        enabled = true,
        onRefresh = {
            onEvent(Event.OnRefresh(selectedTab))
        },
        indicator = {
            PullToRefreshIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
            )
        }
    ) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
            topBar = {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = appBarContainerColor
                    ),
                    navigationIcon = {
                        if (showBackButton){
                            AppNavigationIcon(
                                icon = Icons.ArrowBack,
                                onClick = { onEvent(Event.OnNavigateBack) },
                                contentDescription = stringResource(com.dertefter.design.R.string.design_back_content_desc)
                            )
                        }
                    },
                    title = {
                        userUiState.userDto?.let {
                            Row(
                                modifier = Modifier
                                    .graphicsLayer(
                                        alpha = appBarTitleAlpha
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                            ) {
                                EmojiAvatar(
                                    emoji = it.avatar,
                                    containerSize = 40.dp
                                )
                                Text(
                                    text = it.displayName,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    },
                    actions = {
                        AppNavigationIcon(
                            icon = Icons.Share,
                            onClick = {
                                if (shareText.isNotEmpty()) {
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                }
                            },
                            contentDescription = stringResource(R.string.user_share)
                        )
                        if (userUiState.isMe) {
                            AppNavigationIcon(
                                icon = Icons.Settings,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                onClick = {},
                                contentDescription = stringResource(R.string.user_settings)
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                ) {

                    AnimatedVisibility(
                        visible = isNewPostButtonShow,
                        enter = fadeIn(
                            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                        ) + scaleIn(
                            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                        ) + expandVertically(
                            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                        ),
                        exit = fadeOut(
                            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                        ) + scaleOut(
                            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                        ) + shrinkVertically(
                            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                        )
                    ) {
                        SmallFloatingActionButton(
                            modifier = Modifier.size(addFabSize),
                            shape = RoundedCornerShape(addFabCornerRadius),
                            onClick = {
                                onEvent(Event.OnOpenNewPost)
                            },
                        ) {
                            Icon(Icons.Add, stringResource(R.string.user_create_post))
                        }
                    }


                    AnimatedVisibility(
                        visible = isUpFabVisible,
                        enter = fadeIn(
                            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                        ) + scaleIn(
                            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                        ) + expandVertically(
                            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                        ),
                        exit = fadeOut(
                            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                        ) + scaleOut(
                            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                        ) + shrinkVertically(
                            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                        )
                    ) {
                        SmallFloatingActionButton(
                            modifier = Modifier
                                .padding(top = MaterialTheme.spacing.large)
                                .size(64.dp),
                            shape = RoundedCornerShape(MaterialTheme.rounding.largeIncreased),
                            onClick = {
                                scope.launch {
                                    lazyListState.animateScrollToItem(0)
                                    scrollBehavior.state.contentOffset = 0f
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Icon(Icons.ArrowWarmUp, stringResource(R.string.user_scroll_to_top))
                        }
                    }
                }
            }
        )
        { contentPadding ->
            if (userUiState.userDto != null) {
                val layoutDirection = LocalLayoutDirection.current
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = contentPadding.calculateTopPadding()),
                    contentPadding = PaddingValues(
                        start = contentPadding.calculateStartPadding(layoutDirection),
                        end = contentPadding.calculateEndPadding(layoutDirection),
                        bottom = contentPadding.calculateBottomPadding()
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    if (userUiState.isMe) {
                        item {
                            FilledTonalButton(
                                onClick = {
                                    showAccountSelector = true
                                },
                                modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
                            ) {
                                Icon(
                                    imageVector = Icons.SwapVert,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                Text(
                                    userUiState.currentLogin
                                        ?: stringResource(R.string.user_switch_account)
                                )
                            }
                        }

                    }



                    item {
                        if (MaterialTheme.isFold){
                            HeaderWide(
                                bannerUrl = userUiState.userDto.banner,
                                modifier = Modifier
                                    .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                                    .padding(bottom = MaterialTheme.spacing.large),
                                author = userUiState.userDto.toUiModel(),
                                isMe = userUiState.isMe,
                                scrollBehavior = scrollBehavior,
                                onEditClick = {
                                    if (userUiState.isMe) {
                                        onEvent(Event.OnBannerEdit)
                                    }
                                },
                                onBannerClick = {

                                },
                                onFollowersClick = {
                                    onEvent(Event.OnOpenFollowers(userId = userUiState.userDto.id))
                                },
                                onFollowingClock = {
                                    onEvent(Event.OnOpenFollowing(userId = userUiState.userDto.id))
                                },
                                followersCount = userUiState.userDto.followersCount,
                                followingCount = userUiState.userDto.followingCount,
                            )
                        }else{
                            Header(
                                bannerUrl = userUiState.userDto.banner,
                                modifier = Modifier
                                    .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                                author = userUiState.userDto.toUiModel(),
                                isMe = userUiState.isMe,
                                scrollBehavior = scrollBehavior,
                                onEditClick = {
                                    if (userUiState.isMe) {
                                        onEvent(Event.OnBannerEdit)
                                    }
                                },
                                onBannerClick = {

                                },
                                onFollowersClick = {
                                    onEvent(Event.OnOpenFollowers(userId = userUiState.userDto.id))
                                },
                                onFollowingClock = {
                                    onEvent(Event.OnOpenFollowing(userId = userUiState.userDto.id))
                                },
                                followersCount = userUiState.userDto.followersCount,
                                followingCount = userUiState.userDto.followingCount,

                            )
                        }

                    }

                    if (
                        userUiState.isMe || !userUiState.userDto.bio.isNullOrEmpty()
                    ) {
                        item {
                            BioCard(
                                modifier = Modifier
                                    .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                                    .padding(bottom = MaterialTheme.spacing.large),
                                canEdit = userUiState.isMe,
                                bio = userUiState.userDto.bio ?: "",
                                onSaveClick = {
                                    onEvent(Event.OnSaveBio(it))
                                }
                            )
                        }
                    }

                    if (!userUiState.isMe) {
                        if (userUiState.userDto.isFollowing) {
                            item {
                                FilledTonalButton(
                                    onClick = {
                                        onEvent(Event.OnUnfollow(userUiState.userDto.id))
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                                        .padding(bottom = MaterialTheme.spacing.large)
                                        .fillMaxWidth()
                                ) {
                                    Text(stringResource(R.string.user_unfollow))
                                }
                            }
                        } else {
                            item {
                                Button(
                                    onClick = {
                                        onEvent(Event.OnFollow(userUiState.userDto.id))
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                                        .padding(bottom = MaterialTheme.spacing.large)
                                        .fillMaxWidth()
                                ) {
                                    Text(stringResource(R.string.user_follow))
                                }
                            }
                        }
                    }

                    stickyHeader(key = "tabs") {
                        val stickyHeaderBackground by animateColorAsState(
                            targetValue = if (isStickyHeaderStuck) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface,
                            label = "stickyHeaderBackground"
                        )
                        ButtonGroup(
                            overflowIndicator = { ButtonGroupDefaults.OverflowIndicator(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(stickyHeaderBackground)
                                .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                                .padding(bottom = MaterialTheme.spacing.large),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                        ) {
                            val groupScope = this
                            tabs.forEachIndexed { index, title ->

                                customItem(
                                    buttonGroupContent = {
                                        ToggleButton(
                                            checked = selectedTab == title,
                                            onCheckedChange = {
                                                if (it) {
                                                    onEvent(Event.OnTabSelected(title))
                                                }
                                            },
                                            shapes = when (index) {
                                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                                tabs.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                            },
                                            modifier = with(groupScope) { Modifier.weight(1f) }
                                        ) {
                                            val text = when (title) {
                                                FeedTab.POSTS -> stringResource(R.string.user_posts)
                                                FeedTab.LIKES -> stringResource(R.string.user_liked)
                                            }
                                            val checked = selectedTab == title

                                            val animatedWeight by animateFloatAsState(
                                                targetValue = if (checked) 900f else 500f,
                                                label = "WeightAnimation"
                                            )

                                            val variableFontFamily = FontFamily(
                                                Font(
                                                    resId = com.dertefter.design.R.font.google_sans,
                                                    variationSettings = FontVariation.Settings(
                                                        FontVariation.weight(animatedWeight.toInt()),
                                                    )
                                                )
                                            )

                                            Text(
                                                text,
                                                fontFamily = variableFontFamily,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    },
                                    menuContent = { menuState ->
                                        DropdownMenuItem(
                                            text = {
                                                val text = when (title) {
                                                    FeedTab.POSTS -> stringResource(R.string.user_posts)
                                                    FeedTab.LIKES -> stringResource(R.string.user_liked)
                                                }
                                                Text(text)
                                            },
                                            onClick = {
                                                onEvent(Event.OnTabSelected(title))
                                                menuState.dismiss()
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }

                    if (currentTabUiState != null && paged != null) {
                        feed(
                            uiState = currentTabUiState,
                            isMe = userUiState.isMe && selectedTab == FeedTab.POSTS,
                            paged = paged,
                            onEvent = onEvent
                        )
                    }
                }
            } else {
                if (userUiState.isLoading) {
                    Box(
                        Modifier
                            .padding(contentPadding)
                            .fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        AppLoadingIndicator()
                    }
                } else if (userUiState.error != null) {
                    Box(
                        Modifier
                            .padding(contentPadding)
                            .fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        ErrorLarge(
                            onRetry = { onEvent(Event.OnRefresh(tabs.first())) }
                        )
                    }
                }
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun UserScreenPreview() {
    AppTheme {
        UserScreen(
            onEvent = {},
            userUiState = UserUiState(
                userDto = UserDto(
                    avatar = "🥳",
                    banner = "https://example.com/banner.png",
                    bio = "Developer & Designer",
                    createdAt = "2023-01-01T00:00:00Z",
                    displayName = "Dertefter Labs",
                    followersCount = 120,
                    followingCount = 80,
                    id = "1",
                    hasNuksta = false,
                    isFollowedBy = false,
                    isFollowing = false,
                    lastSeen = null,
                    likesVisibility = VisibilityDto.EVERYONE,
                    online = true,
                    pin = null,
                    pinnedPostId = null,
                    postsCount = 42,
                    username = "dertefter",
                    verified = true,
                    wallAccess = VisibilityDto.EVERYONE
                ),
                isLoading = false
            ),
            selectedTab = FeedTab.POSTS,
            uiStates = emptyMap(),
            showBackButton = false
        )
    }
}
