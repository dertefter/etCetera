package com.dertefter.followers.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.followers.R
import com.dertefter.followers.presentation.component.FollowerUserCard
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.compose.paginated
import com.jamal_aliev.paginator.compose.rememberPaginated
import com.jamal_aliev.paginator.extension.isErrorState
import com.jamal_aliev.paginator.extension.isProgressState
import com.jamal_aliev.paginator.page.PaginatorUiState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Feed(
    paginator: MutableCursorPaginator<FollowerUserDto>,
    selectedTab: Tab,
    onEvent: (Event) -> Unit,
    uiState: PaginatorUiState<FollowerUserDto>,
    contentPadding: PaddingValues = PaddingValues(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    listState: LazyListState = rememberLazyListState()
) {
    val paged = paginator.rememberPaginated(state = listState)
    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = uiState is PaginatorUiState.Content && uiState.prependState.isProgressState()

    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { onEvent(Event.OnRefresh(selectedTab)) },
        indicator = {
            PullToRefreshIndicator(
                modifier = Modifier
                    .padding(top = contentPadding.calculateTopPadding())
                    .align(Alignment.TopCenter),
                state = pullToRefreshState,
                isRefreshing = isRefreshing
            )
        }
    ) {

        AnimatedContent(
            targetState = uiState,
            contentKey = { it::class },
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "feed_state",
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ){ state ->
            when (state) {
                PaginatorUiState.Idle -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        AppLoadingIndicator()
                    }
                }

                is PaginatorUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        AppLoadingIndicator()
                    }
                }

                is PaginatorUiState.Empty -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Постов пока нет")
                    }
                }

                is PaginatorUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ошибка загрузки: ${state.exception.message}")
                    }
                }

                is PaginatorUiState.Content -> {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .then(
                                if (scrollBehavior != null) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                                else Modifier
                            ),
                        state = listState
                    ) {
                        paginated(paged) {
                            itemsIndexed(
                                state.items,
                                key = { _, user -> user.id }) { index, followerUser ->
                                FollowerUserCard(
                                    followerUser = followerUser,
                                    onClick = { onEvent(Event.OnOpenUser(followerUser.id)) },
                                    onFollow = { onEvent(Event.OnFollow(followerUser.id)) },
                                    onUnfollow = { onEvent(Event.OnUnfollow(followerUser.id)) }
                                )

                            }

                            appendIndicator {
                                state.appendState?.let { appendState ->
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (appendState.isProgressState()) {
                                            CircularProgressIndicator()
                                        } else if (appendState.isErrorState()) {
                                            Text(stringResource(R.string.followers_empty))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
