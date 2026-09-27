package com.dertefter.followers.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.style.TextOverflow
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.appbar.AppTopBarStyle
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.spacing
import com.dertefter.followers.R
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
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

    val hazeState = rememberHazeState()

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
                AppTopBar(
                    hazeState = hazeState,
                    appTopBarStyle = AppTopBarStyle.SMALL,
                    title = {},
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
                    },
                    supportingContent = {
                        ButtonGroup(
                            overflowIndicator = { ButtonGroupDefaults.OverflowIndicator(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                                .padding(bottom = MaterialTheme.spacing.small),
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
                )
            }
        ) { contentPadding ->
            Box(Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .hazeSource(hazeState)
                        .fillMaxSize(),
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