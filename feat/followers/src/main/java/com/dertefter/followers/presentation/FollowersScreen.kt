package com.dertefter.followers.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.cornerShape
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing
import com.dertefter.followers.R
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.page.PaginatorUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@Composable
fun FollowersScreen(
    onEvent: (Event) -> Unit,
    selectedTab: Tab,
    uiStates: Map<Tab, PaginatorUiState<FollowerUserDto>>,
    paginators: Map<Tab, MutableCursorPaginator<FollowerUserDto>>
) {
    val tabs = Tab.entries

    val followersScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val followingScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val followersListState = rememberLazyListState()
    val followingListState = rememberLazyListState()

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
                                    Text(text)
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
    },
    ) { contentPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val tab = tabs[page]
            Feed(
                paginator = paginators[tab]!!,
                selectedTab = tab,
                onEvent = onEvent,
                uiState = uiStates[tab]!!,
                contentPadding = contentPadding,
                scrollBehavior = scrollBehaviors[tab]!!,
                listState = listStates[tab]!!
            )
        }
    }

}
