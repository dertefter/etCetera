package com.dertefter.user.presentation

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.user.UserDto
import com.dertefter.data.dto.user.VisibilityDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.common.ErrorLarge
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.cornerShape
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing
import com.dertefter.user.R
import com.dertefter.user.presentation.component.BioCard
import com.dertefter.user.presentation.component.Header
import com.dertefter.user.presentation.component.TitleValueCard
import com.dertefter.user.presentation.mapper.toUiModel
import com.jamal_aliev.paginator.compose.cursor.rememberPaginated
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import com.dertefter.design.R as DesignR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@Composable
fun UserScreen(
    onEvent: (Event) -> Unit,
    uiState: UiState,
    paginators: Map<FeedTab, MutableCursorPaginator<String, PostDto>> = emptyMap(),
) {

    val context = LocalContext.current

    val lazyListState = rememberLazyListState()

    val scope = rememberCoroutineScope()

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

    val isStickyHeaderStuck by remember {
        derivedStateOf {
            val stickyHeaderIndex = 3
            lazyListState.firstVisibleItemIndex >= stickyHeaderIndex
        }
    }


    val isNewPostButtonShow by remember(uiState) {
        derivedStateOf {
            val user = uiState.userDto
            if (user != null) {
                uiState.isMe || when (user.wallAccess) {
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

    val paginator = paginators[uiState.selectedTab]
    val paged = paginator?.rememberPaginated(state = lazyListState)

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val currentTabUiState = uiState.uiStates[uiState.selectedTab]
    val isRefreshing = uiState.isLoading || (currentTabUiState is PaginatorUiState.Content && currentTabUiState.prependState.isProgressState())
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(lazyListState) {
        delay(2000.milliseconds)
        while (true) {
            val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
            val visibleIds = visibleItems.mapNotNull {
                it.key.toString().takeIf { key -> key.length > 5 }
            }
            if (visibleIds.isNotEmpty()) {
                onEvent(Event.OnUpdateStats(visibleIds))
            }
            delay(5000.milliseconds)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    AppNavigationIcon(
                        icon = Icons.ArrowBack,
                        onClick = { onEvent(Event.OnNavigateBack) },
                        contentDescription = stringResource(DesignR.string.design_back_content_desc)
                    )

                },
                title = {
                    uiState.userDto?.let {
                        Row(
                            modifier = Modifier
                                .graphicsLayer(
                                    alpha = scrollBehavior.state.overlappedFraction
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                        ) {
                            SmallEmojiAvatar(
                                emoji = it.avatar,
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
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
                            uiState.userDto?.let { user ->
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "https://итд.com/@${user.username}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            }
                        },
                        contentDescription = stringResource(R.string.user_share)
                    )
                    if (uiState.isMe){
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
                        shape = MaterialTheme.cornerShape(addFabCornerRadius),
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
                        shape = MaterialTheme.cornerShape(MaterialTheme.rounding.largeIncreased),
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
    ) { contentPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize(),
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = {
                onEvent(Event.OnRefresh(uiState.selectedTab))
            },
            indicator = {
                PullToRefreshIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing
                )
            }
        ) {
            if (uiState.userDto != null) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .fillMaxSize(),
                    contentPadding = contentPadding,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
                ) {
                    item {
                        Header(
                            bannerUrl = uiState.userDto.banner,
                            modifier = Modifier
                                .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                            author = uiState.userDto.toUiModel(),
                            isMe = uiState.isMe,
                            scrollBehavior = scrollBehavior,
                            onEditClick = {
                                if (uiState.isMe){
                                    onEvent(Event.OnBannerEdit)
                                }
                            },
                            onBannerClick = {

                            }
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.defaultScreenPadding)
                        ) {
                            TitleValueCard(
                                title = stringResource(R.string.user_followers),
                                value = uiState.userDto.followersCount,
                                onClick = {
                                    onEvent(Event.OnOpenFollowers(userId = uiState.userDto.id))
                                }
                            )

                            TitleValueCard(
                                title = stringResource(R.string.user_following),
                                value = uiState.userDto.followingCount,
                                onClick = {
                                    onEvent(Event.OnOpenFollowing(userId = uiState.userDto.id))
                                }
                            )

                        }
                    }

                    if (
                        uiState.isMe || !uiState.userDto.bio.isNullOrEmpty()
                    ){
                        item {
                            BioCard(
                                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                                canEdit = uiState.isMe,
                                bio = uiState.userDto.bio ?: "",
                                onSaveClick = {
                                    onEvent(Event.OnSaveBio(it))
                                }
                            )
                        }
                    }

                    if (!uiState.isMe){
                        if (uiState.userDto.isFollowing){
                           item{
                               FilledTonalButton(
                                   onClick = {
                                       onEvent(Event.OnUnfollow(uiState.userDto.id))
                                   },
                                   modifier = Modifier
                                       .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                                       .fillMaxWidth()
                               ) {
                                   Text(stringResource(R.string.user_unfollow))
                               }
                           }
                        }else{
                            item{
                                Button(
                                    onClick = {
                                        onEvent(Event.OnFollow(uiState.userDto.id))
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                                        .fillMaxWidth()
                                ) {
                                    Text(stringResource(R.string.user_follow))
                                }
                            }
                        }
                    }

                    stickyHeader {
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
                                .padding(bottom = MaterialTheme.spacing.small),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                        ) {
                            val groupScope = this
                            tabs.forEachIndexed { index, title ->

                                customItem(
                                    buttonGroupContent = {
                                        ToggleButton(
                                            checked = uiState.selectedTab == title,
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
                                            Text(text)
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
                            isMe = uiState.isMe && uiState.selectedTab == FeedTab.POSTS,
                            paged = paged,
                            onEvent = onEvent,
                            pinnedPostId = if (uiState.selectedTab == FeedTab.POSTS) uiState.userDto.pinnedPostId else null
                        )
                    }
                }
            } else {
                if (uiState.isLoading) {
                    Box(Modifier
                        .padding(contentPadding)
                        .fillMaxSize(), contentAlignment = Alignment.Center) {
                        AppLoadingIndicator()
                    }
                } else if (uiState.error != null) {
                    Box(Modifier
                        .padding(contentPadding)
                        .fillMaxSize(), contentAlignment = Alignment.Center) {
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
            uiState = UiState(
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
        )
    }
}
