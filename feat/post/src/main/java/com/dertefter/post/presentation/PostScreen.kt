package com.dertefter.post.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.comments.CommentsViewModel
import com.dertefter.comments.presentation.Comments
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.components.post.PostCard
import com.dertefter.design.theme.spacing
import com.dertefter.post.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PostScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
    commentsViewModel: CommentsViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()
    val selectedTab by commentsViewModel.selectedTab.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(stringResource(R.string.post_title))
                },
                navigationIcon = {
                    AppNavigationIcon(
                        onClick = { onEvent(Event.OnNavigateBack) }
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { contentPadding ->
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            state = pullToRefreshState,
            isRefreshing = uiState.isLoading,
            onRefresh = {
                onEvent(Event.OnRefresh)
                uiState.post?.let { post ->
                    commentsViewModel.onEvent(com.dertefter.comments.presentation.Event.OnRefresh(selectedTab, post.id))
                }
            },
            indicator = {
                PullToRefreshIndicator(
                    modifier = Modifier
                        .padding(top = contentPadding.calculateTopPadding())
                        .align(Alignment.TopCenter),
                    state = pullToRefreshState,
                    isRefreshing = uiState.isLoading
                )
            }
        ) {
            if (uiState.isLoading && uiState.post == null) {
                Box(
                    Modifier
                        .padding(contentPadding)
                        .fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    AppLoadingIndicator()
                }
            } else {
                uiState.post?.let { post ->
                    val commentsUiState by commentsViewModel.getUiState(post.id, selectedTab)
                        .collectAsStateWithLifecycle()
                    Comments(
                        postId = post.id,
                        paginator = commentsViewModel.getPaginator(post.id, selectedTab),
                        selectedTab = selectedTab,
                        onEvent = commentsViewModel::onEvent,
                        uiState = commentsUiState,
                        contentPadding = contentPadding,
                        scrollBehavior = scrollBehavior,
                        header = {
                            item {
                                PostCard(
                                    post = post,
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    onLike = { onEvent(Event.OnLike) },
                                    onUnlike = { onEvent(Event.OnUnlike) },
                                    onUserClick = { userId -> onEvent(Event.OnOpenUser(userId)) },
                                    onVote = { optionIds -> onEvent(Event.OnVote(optionIds)) },
                                    onOpenPost = { onEvent(Event.OnOpenPost(it)) }
                                )
                            }
                            item {
                                Text(
                                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                                    text = stringResource(R.string.post_comments),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

