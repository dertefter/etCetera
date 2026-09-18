package com.dertefter.followers.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.followers.R
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@Composable
fun FollowersScreen(
    onEvent: (Event) -> Unit,
    selectedTab: Tab,
    uiStates: Map<Tab, PaginatorUiState<FollowerUserDto>>,
    paginators: Map<Tab, MutableCursorPaginator<String, FollowerUserDto>?>
) {
    val tabs = Tab.entries

    val followersScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val followingScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val followersListState = rememberLazyGridState()
    val followingListState = rememberLazyGridState()

    var isGrid by rememberSaveable { mutableStateOf(true) }

    val scrollBehaviors = mapOf(
        Tab.FOLLOWERS to followersScrollBehavior,
        Tab.FOLLOWING to followingScrollBehavior
    )

    val listStates = mapOf(
        Tab.FOLLOWERS to followersListState,
        Tab.FOLLOWING to followingListState
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
        followersScrollBehavior.state.heightOffset,
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
                val containerColor = lerp(
                    MaterialTheme.colorScheme.surface,
                    MaterialTheme.colorScheme.surfaceContainer,
                    scrollBehavior.state.overlappedFraction
                )
                Surface(color = containerColor) {
                    Column {
                        TopAppBar(
                            title = {},
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                scrolledContainerColor = Color.Transparent
                            ),
                            scrollBehavior = scrollBehavior,
                            navigationIcon = {
                                AppNavigationIcon(
                                    onClick = {
                                        onEvent(Event.OnBackClick)
                                    }
                                )
                            },
                            actions = {
                                AppNavigationIcon(
                                    icon = if (isGrid) Icons.List else Icons.GridView,
                                    onClick = {
                                        isGrid = !isGrid
                                    }
                                )
                            }

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
                                            val text = when (title) {
                                                Tab.FOLLOWERS -> stringResource(R.string.followers_tab_followers)
                                                Tab.FOLLOWING -> stringResource(R.string.followers_tab_following)
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
                                                    Tab.FOLLOWERS -> stringResource(R.string.followers_tab_followers)
                                                    Tab.FOLLOWING -> stringResource(R.string.followers_tab_following)
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
            }
        ) { contentPadding ->
            Box(Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = false
                ) { page ->
                    val tab = tabs[page]
                    paginators[tab]?.let { paginator ->
                        Feed(
                            paginator = paginator,
                            onEvent = onEvent,
                            uiState = uiStates[tab]!!,
                            contentPadding = contentPadding,
                            scrollBehavior = scrollBehaviors[tab]!!,
                            listState = listStates[tab]!!,
                            isGrid = isGrid
                        )
                    }
                }
            }
        }
    }
}

@Preview(locale = "ru")
@Composable
fun FollowersScreenPreview() {
    AppTheme {
        val sampleFollowers = listOf(
            FollowerUserDto(
                id = "1",
                username = "johndoe",
                displayName = "John Doe",
                avatar = "🍃",
                verified = true,
                isFollowing = true
            ),
            FollowerUserDto(
                id = "2",
                username = "janedoe",
                displayName = "Jane Doe",
                avatar = "🦐",
                verified = false,
                isFollowing = false
            ),
            FollowerUserDto(
                id = "3",
                username = "alexsmith",
                displayName = "Alex Smith",
                avatar = "❤️",
                verified = true,
                isFollowing = false
            ),
            FollowerUserDto(
                id = "4",
                username = "alexsmith",
                displayName = "Alex Smith",
                avatar = "💙",
                verified = true,
                isFollowing = false
            )
        )

        val samplePaginator = mutableCursorPaginator<String, FollowerUserDto> {
            load {
                CursorLoadResult(
                    data = sampleFollowers,
                    bookmark = CursorBookmark(null, "initial", null)
                )
            }
        }

        val paginators = mapOf(
            Tab.FOLLOWERS to samplePaginator,
            Tab.FOLLOWING to samplePaginator
        )

        val uiStates = mapOf(
            Tab.FOLLOWERS to PaginatorUiState.Content(
                prependState = null,
                items = sampleFollowers,
                appendState = null
            ),
            Tab.FOLLOWING to PaginatorUiState.Content(
                prependState = null,
                items = sampleFollowers,
                appendState = null
            )
        )

        FollowersScreen(
            onEvent = {},
            selectedTab = Tab.FOLLOWERS,
            uiStates = uiStates,
            paginators = paginators
        )
    }
}