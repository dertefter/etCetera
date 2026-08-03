package com.dertefter.feed.presentation

import android.util.Log
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing
import com.dertefter.feed.R
import com.dertefter.feed.presentation.component.FeedAppBar
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@Composable
fun FeedScreen(
    onEvent: (Event) -> Unit,
    selectedTab: FeedTab,
    uiStates: Map<FeedTab, PaginatorUiState<PostDto>>,
    paginators: Map<FeedTab, MutableCursorPaginator<String, PostDto>>,
    topAppBarState: TopBarUiState
) {
    val tabs = FeedTab.entries


    val popularListState = rememberLazyStaggeredGridState()
    val clanListState = rememberLazyStaggeredGridState()
    val followingListState = rememberLazyStaggeredGridState()

    val popularScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val clanScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val followingScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val scrollBehaviors = mapOf(
        FeedTab.POPULAR to popularScrollBehavior,
        FeedTab.CLAN to clanScrollBehavior,
        FeedTab.FOLLOWING to followingScrollBehavior
    )

    val gridStates = mapOf(
        FeedTab.POPULAR to popularListState,
        FeedTab.CLAN to clanListState,
        FeedTab.FOLLOWING to followingListState
    )

    val pagerState = rememberPagerState(
        pageCount = { tabs.size },
        initialPage = tabs.indexOf(selectedTab).coerceAtLeast(0)
    )

    val pullToRefreshState = rememberPullToRefreshState()
    val currentTab = tabs[pagerState.currentPage]
    val currentUiState = uiStates[currentTab]
    val isRefreshing = currentUiState is PaginatorUiState.Content && currentUiState.prependState.isProgressState()

    LaunchedEffect(
        popularScrollBehavior.state.heightOffset,
        clanScrollBehavior.state.heightOffset,
        followingScrollBehavior.state.heightOffset
    ) {
        val currentTab = tabs[pagerState.currentPage]
        val currentOffset = scrollBehaviors[currentTab]?.state?.heightOffset ?: 0f
        tabs.forEach { tab ->
            if (tab != currentTab) {
                scrollBehaviors[tab]?.state?.heightOffset = currentOffset
            }
        }
    }

    val scrollBehavior = scrollBehaviors[tabs[pagerState.currentPage]]!!
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        onEvent(Event.OnTabSelected(tabs[pagerState.currentPage]))
    }

    val currentListState = gridStates[tabs[pagerState.currentPage]]!!

    val isUpFabVisible by remember(currentListState) {
        derivedStateOf {
            currentListState.firstVisibleItemIndex > 3
        }
    }

    val addFabSize by animateDpAsState(
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        targetValue = if (isUpFabVisible) 40.dp else 64.dp
    )

    val addFabCornerRadius by animateDpAsState(
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        targetValue = if (isUpFabVisible) MaterialTheme.rounding.medium else MaterialTheme.rounding.largeIncreased
    )

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { onEvent(Event.OnRefresh(currentTab)) },
        indicator = {
            PullToRefreshIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter),
                state = pullToRefreshState,
                isRefreshing = isRefreshing
            )
        }
    ) {
        Scaffold(
            topBar = {

                Log.e("overlappedFraction,overlappedFraction", scrollBehavior.state.overlappedFraction.toString())

                val containerColor by animateColorAsState(
                    if (scrollBehavior.state.overlappedFraction <= 0.99f) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceContainer
                )
                Surface(color = containerColor) {
                    Column {
                        FeedAppBar(
                            profileEmoji = topAppBarState.avatarEmoji,
                            popularHashtags = topAppBarState.trendingHashtags,
                            scrollBehavior = scrollBehavior,
                            onProfileClick = {
                                topAppBarState.userId?.let{ userId ->
                                    onEvent(Event.OnOpenUser(userId))
                                }
                            },
                            onNotificationsClick = { onEvent(Event.OnOpenNotifications) },
                            onSearchClick = { onEvent(Event.OnOpenSearch) }
                        )
                        ButtonGroup(
                            overflowIndicator = { ButtonGroupDefaults.OverflowIndicator(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                                .padding(bottom = MaterialTheme.spacing.small),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                        ) {
                            val groupScope = this
                            tabs.forEachIndexed { index, title ->

                                customItem(
                                    buttonGroupContent = {
                                        ToggleButton(
                                            checked = pagerState.currentPage == index,
                                            onCheckedChange = {
                                                if (it) {
                                                    scope.launch {
                                                        pagerState.animateScrollToPage(index)
                                                    }
                                                }
                                            },
                                            shapes = when (index) {
                                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                                tabs.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                            },
                                            modifier = with(groupScope) { Modifier.weight(1f) }
                                        ) {

                                            val checked = pagerState.currentPage == index

                                            val text = when (title) {
                                                FeedTab.POPULAR -> stringResource(R.string.feed_popular)
                                                FeedTab.CLAN -> stringResource(R.string.feed_clan)
                                                FeedTab.FOLLOWING -> stringResource(R.string.feed_following)
                                            }

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
                                                    FeedTab.POPULAR -> stringResource(R.string.feed_popular)
                                                    FeedTab.CLAN -> stringResource(R.string.feed_clan)
                                                    FeedTab.FOLLOWING -> stringResource(R.string.feed_following)
                                                }
                                                Text(text)
                                            },
                                            onClick = {
                                                scope.launch {
                                                    pagerState.animateScrollToPage(index)
                                                }
                                                menuState.dismiss()
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    SmallFloatingActionButton(
                        modifier = Modifier.size(addFabSize),
                        shape = RoundedCornerShape(addFabCornerRadius),
                        onClick = { onEvent(Event.OnOpenNewPost) },
                    ) {
                        Icon(Icons.Add, stringResource(R.string.feed_create_post))
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
                            shape = MaterialTheme.shapes.largeIncreased,
                            onClick = {
                                scope.launch {
                                    currentListState.animateScrollToItem(0)
                                }
                                scrollBehavior.state.heightOffset = 0f
                                scrollBehavior.state.contentOffset = 0f
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Icon(Icons.ArrowWarmUp, stringResource(R.string.feed_scroll_to_top))
                        }
                    }
                }
            }
        ) { contentPadding ->
            Box(Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = false
                ) { page ->
                    val tab = tabs[page]
                    Feed(
                        paginator = paginators[tab]!!,
                        onEvent = onEvent,
                        uiState = uiStates[tab]!!,
                        contentPadding = contentPadding,
                        scrollBehavior = scrollBehaviors[tab]!!,
                        gridState = gridStates[tab]!!
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE
)
@Composable
fun FeedScreenPreview() {
    AppTheme {
        val sampleAuthor = AuthorDto(
            id = "1",
            avatar = "😊",
            username = "johndoe",
            verified = true,
            hasNuksta = false,
            displayName = "John Doe"
        )

        val samplePosts = listOf(
            PostDto(
                id = "1",
                content = "This is a sample post",
                spans = emptyList(),
                likesCount = 10,
                commentsCount = 2,
                repostsCount = 1,
                viewsCount = 100,
                author = sampleAuthor,
                attachments = emptyList(),
                isLiked = false,
                isReposted = false,
                isOwner = false,
                isViewed = true,
                createdAt = "2023-10-27T10:00:00Z",
                vs = ""
            ),
            PostDto(
                id = "2",
                content = "Another sample post",
                spans = emptyList(),
                likesCount = 5,
                commentsCount = 0,
                repostsCount = 0,
                viewsCount = 50,
                author = sampleAuthor,
                attachments = emptyList(),
                isLiked = true,
                isReposted = false,
                isOwner = false,
                isViewed = true,
                createdAt = "2023-10-27T11:00:00Z",
                vs = ""
            ),
            PostDto(
                id = "3",
                content = "Another sample post",
                spans = emptyList(),
                likesCount = 5,
                commentsCount = 0,
                repostsCount = 0,
                viewsCount = 50,
                author = sampleAuthor,
                attachments = emptyList(),
                isLiked = true,
                isReposted = false,
                isOwner = false,
                isViewed = true,
                createdAt = "2023-10-27T11:00:00Z",
                vs = ""
            ),
            PostDto(
                id = "4",
                content = "Another sample post",
                spans = emptyList(),
                likesCount = 5,
                commentsCount = 0,
                repostsCount = 0,
                viewsCount = 50,
                author = sampleAuthor,
                attachments = emptyList(),
                isLiked = true,
                isReposted = false,
                isOwner = false,
                isViewed = true,
                createdAt = "2023-10-27T11:00:00Z",
                vs = ""
            ),
            PostDto(
                id = "5",
                content = "Another sample post",
                spans = emptyList(),
                likesCount = 5,
                commentsCount = 0,
                repostsCount = 0,
                viewsCount = 50,
                author = sampleAuthor,
                attachments = emptyList(),
                isLiked = true,
                isReposted = false,
                isOwner = false,
                isViewed = true,
                createdAt = "2023-10-27T11:00:00Z",
                vs = ""
            ),
            PostDto(
                id = "7",
                content = "Another sample post",
                spans = emptyList(),
                likesCount = 5,
                commentsCount = 0,
                repostsCount = 0,
                viewsCount = 50,
                author = sampleAuthor,
                attachments = emptyList(),
                isLiked = true,
                isReposted = false,
                isOwner = false,
                isViewed = true,
                createdAt = "2023-10-27T11:00:00Z",
                vs = ""
            )
        )

        val samplePaginator = mutableCursorPaginator {
            load {
                CursorLoadResult(
                    data = samplePosts,
                    bookmark = CursorBookmark(null, "initial", null)
                )
            }
        }
        val paginators = mapOf(
            FeedTab.POPULAR to samplePaginator,
            FeedTab.CLAN to samplePaginator,
            FeedTab.FOLLOWING to samplePaginator
        )
        val uiStates = mapOf(
            FeedTab.POPULAR to PaginatorUiState.Content(
                prependState = null,
                items = samplePosts,
                appendState = null
            ),
            FeedTab.CLAN to PaginatorUiState.Content(
                prependState = null,
                items = samplePosts,
                appendState = null
            ),
            FeedTab.FOLLOWING to PaginatorUiState.Content(
                prependState = null,
                items = samplePosts,
                appendState = null
            )
        )

        FeedScreen(
            onEvent = {},
            selectedTab = FeedTab.POPULAR,
            uiStates = uiStates,
            topAppBarState = TopBarUiState(),
            paginators = paginators,
        )
    }
}
