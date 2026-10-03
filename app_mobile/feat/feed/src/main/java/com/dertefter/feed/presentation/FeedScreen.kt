package com.dertefter.feed.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.buttons.FloatingAction
import com.dertefter.design.components.buttons.FloatingActions
import com.dertefter.design.components.buttons.FloatingActionsScrolledStatus
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.spacing
import com.dertefter.feed.R
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(
    onEvent: (Event) -> Unit,
    selectedTab: FeedTab,
    uiStates: Map<FeedTab, PaginatorUiState<PostDto>>,
    paginators: Map<FeedTab, MutableCursorPaginator<String, PostDto>>
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

    val hazeState = rememberHazeState()

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

    val floatingActionsScrolledStatus by remember(currentListState) {
        derivedStateOf {
            when {
                currentListState.firstVisibleItemIndex > 3 -> FloatingActionsScrolledStatus.MORE_SCROLLED
                scrollBehavior.state.overlappedFraction > 0f -> FloatingActionsScrolledStatus.SCROLLED
                else -> FloatingActionsScrolledStatus.IDLE
            }
        }
    }

    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize(),
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
                AppTopBar(
                    hazeState = hazeState,
                    title = {
                        Text(stringResource(R.string.feed_title))
                    },
                    scrollBehavior = scrollBehavior,
                    supportingContent = {
                        ButtonGroup(
                            overflowIndicator = { ButtonGroupDefaults.OverflowIndicator(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = MaterialTheme.spacing.small)
                                .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                        )
                        {
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
                )

            },
            floatingActionButton = {
                FloatingActions(
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.bottomNavHeight),
                    floatingActionsScrolledStatus = floatingActionsScrolledStatus,
                    primaryFloatingAction = FloatingAction(
                        icon = Icons.Add,
                        contentDescription = stringResource(R.string.feed_create_post),
                        onClick = { onEvent(Event.OnOpenNewPost) }
                    ),
                    secondaryFloatingAction = FloatingAction(
                        icon = Icons.ArrowWarmUp,
                        contentDescription = stringResource(R.string.feed_scroll_to_top),
                        onClick = {
                            scope.launch {
                                currentListState.animateScrollToItem(0)
                            }
                            scrollBehavior.state.heightOffset = 0f
                            scrollBehavior.state.contentOffset = 0f
                        }
                    )
                )
            }
        ) { contentPadding ->
            Box(Modifier
                .fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .hazeSource(hazeState)
                        .fillMaxSize(),
                    userScrollEnabled = false
                ) { page ->
                    val tab = tabs[page]
                    Feed(
                        paginator = paginators[tab]!!,
                        onEvent = onEvent,
                        uiState = uiStates[tab]!!,
                        contentPadding = contentPadding
                                + PaddingValues(bottom = MaterialTheme.bottomNavHeight),
                        scrollBehavior = scrollBehaviors[tab]!!,
                        gridState = gridStates[tab]!!
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE
)
@Composable
fun FeedScreenPreview() {
    AppTheme(
        content = {
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
                paginators = paginators,
            )
        },
    )
}
