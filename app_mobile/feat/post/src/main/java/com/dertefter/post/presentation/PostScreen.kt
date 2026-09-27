package com.dertefter.post.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.comments.CommentsViewModel
import com.dertefter.comments.presentation.CommentSort
import com.dertefter.comments.presentation.CommentsFeed
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.post.PostCard
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.spacing
import com.dertefter.post.R
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import com.dertefter.comments.presentation.Event as CommentsEvent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PostScreen(
    uiState: UiState,
    meUserId: String?,
    onEvent: (Event) -> Unit,
    commentsViewModel: CommentsViewModel = hiltViewModel()
) {
    val selectedTab by commentsViewModel.selectedTab.collectAsStateWithLifecycle()
    val commentsUiState by if (uiState.post != null) {
        commentsViewModel.getUiState(uiState.post.id, selectedTab).collectAsStateWithLifecycle()
    } else {
        remember { mutableStateOf(PaginatorUiState.Idle) }
    }

    PostScreenContent(
        uiState = uiState,
        meUserId = meUserId,
        onEvent = onEvent,
        commentSort = selectedTab,
        commentsUiState = commentsUiState,
        commentsPaginator = if (uiState.post != null) commentsViewModel.getPaginator(
            uiState.post.id,
            selectedTab
        ) else null,
        onCommentsEvent = commentsViewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PostScreenContent(
    uiState: UiState,
    meUserId: String?,
    onEvent: (Event) -> Unit,
    commentSort: CommentSort,
    commentsUiState: PaginatorUiState<CommentDto>,
    commentsPaginator: MutableCursorPaginator<String, CommentDto>?,
    onCommentsEvent: (CommentsEvent) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()
    var showMenu by remember { mutableStateOf(false) }

    var pullRefreshing by remember { mutableStateOf(false) }

    val hazeState = rememberHazeState()

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            pullRefreshing = false
        }
    }

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = pullRefreshing,
        onRefresh = {
            pullRefreshing = true
            onEvent(Event.OnRefresh)
            uiState.post?.let { post ->
                onCommentsEvent(CommentsEvent.OnRefresh(commentSort, post.id))
            }
        },
        indicator = {
            PullToRefreshIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter),
                state = pullToRefreshState,
                isRefreshing = pullRefreshing
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    hazeState = hazeState,
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
            uiState.post?.let { post ->
                key(commentSort) {
                    if (commentsPaginator != null) {
                        CommentsFeed(
                            meUserId = meUserId,
                            paginator = commentsPaginator,
                            onEvent = onCommentsEvent,
                            uiState = commentsUiState,
                            contentPadding = contentPadding + PaddingValues(bottom = MaterialTheme.bottomNavHeight),
                            scrollBehavior = scrollBehavior,
                            header = {
                                item(key = "post_card_${post.id}") {
                                    PostCard(
                                        post = post,
                                        onLike = { onEvent(Event.OnLike) },
                                        onUnlike = { onEvent(Event.OnUnlike) },
                                        onUserClick = { userId -> onEvent(Event.OnOpenUser(userId)) },
                                        onVote = { optionIds -> onEvent(Event.OnVote(optionIds)) },
                                        onOpenPost = { postId -> onEvent(Event.OnOpenPost(postId)) },
                                        onAttachmentClick = { attachments, position ->
                                            onEvent(
                                                Event.OnOpenAttachmentsViewer(
                                                    attachments,
                                                    position
                                                )
                                            )
                                        },
                                        onHashtagClick = {
                                            onEvent(Event.OnOpenHashtag(it))
                                        },
                                        showCommentsButton = false,
                                        onDelete = { onEvent(Event.OnDeletePost(post.id)) },
                                        onEdit = { onEvent(Event.OnEditPost(it)) },
                                        onCommentsClick = {},
                                        onPin = { onEvent(Event.OnPin(post.id)) },
                                        onUnpin = { onEvent(Event.OnUnpin(post.id)) },
                                        onRepostClick = { onEvent(Event.OnRepost(post.id)) },
                                        onReport = { onEvent(Event.OnReport("post", post.id)) }
                                    )
                                }
                                item {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {

                                        Box {
                                            AppNavigationIcon(
                                                onClick = { showMenu = true },
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                icon = Icons.SwapVert,
                                                contentDescription = stringResource(com.dertefter.comments.R.string.comments_sort)
                                            )
                                            DropdownMenu(
                                                expanded = showMenu,
                                                shape = MaterialTheme.shapes.large,
                                                onDismissRequest = { showMenu = false }
                                            ) {
                                                CommentSort.entries.forEach { tab ->

                                                    val text = when (tab) {
                                                        CommentSort.POPULAR -> stringResource(com.dertefter.comments.R.string.comments_popular)
                                                        CommentSort.OLDEST -> stringResource(com.dertefter.comments.R.string.comments_oldest)
                                                        CommentSort.NEWEST -> stringResource(com.dertefter.comments.R.string.comments_newest)
                                                    }

                                                    DropdownMenuItem(
                                                        text = { Text(text) },
                                                        onClick = {
                                                            onCommentsEvent(CommentsEvent.OnTabSelected(tab))
                                                            showMenu = false
                                                        },
                                                        trailingIcon = {
                                                            if (commentSort == tab) {
                                                                Icon(
                                                                    imageVector = Icons.Check,
                                                                    contentDescription = null
                                                                )
                                                            }
                                                        }
                                                    )
                                                }
                                            }
                                        }

                                        Text(
                                            text = stringResource(R.string.post_comments),
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier
                                                .padding(horizontal = MaterialTheme.spacing.large)
                                                .weight(1f)
                                        )

                                        AppNavigationIcon(
                                            onClick = {
                                                onCommentsEvent(CommentsEvent.OnNewComment)
                                            },
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            icon = Icons.Add,
                                            contentDescription = "New comment"
                                        )
                                    }

                                }
                            },
                            modifier = Modifier.hazeSource(hazeState)
                        )
                    }
                }
            }
        }
    }
}

