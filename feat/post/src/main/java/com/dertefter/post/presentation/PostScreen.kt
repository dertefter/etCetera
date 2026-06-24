package com.dertefter.post.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.comments.CommentsViewModel
import com.dertefter.comments.presentation.CommentSort
import com.dertefter.comments.presentation.Comments
import com.dertefter.comments.presentation.Event as CommentsEvent
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.components.post.AuthorUiModel
import com.dertefter.design.components.post.PostCard
import com.dertefter.design.components.post.PostUiModel
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.post.R
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult

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
        selectedTab = selectedTab,
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
    selectedTab: CommentSort,
    commentsUiState: PaginatorUiState<CommentDto>,
    commentsPaginator: MutableCursorPaginator<String, CommentDto>?,
    onCommentsEvent: (CommentsEvent) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
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
                    onCommentsEvent(CommentsEvent.OnRefresh(selectedTab, post.id))
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
                    key(selectedTab) {
                        if (commentsPaginator != null) {
                            Comments(
                                meUserId = meUserId,
                                paginator = commentsPaginator,
                                onEvent = onCommentsEvent,
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
                                            onOpenPost = { onEvent(Event.OnOpenPost(it)) },
                                            onAttachmentClick = { attachments, position ->
                                                onEvent(
                                                    Event.OnOpenAttachmentsViewer(
                                                        attachments,
                                                        position
                                                    )
                                                )
                                            },
                                            showCommentsButton = false
                                        )
                                    }
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ){

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
                                                                if (selectedTab == tab) {
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
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostScreenPreview() {
    AppTheme {
        val sampleAuthor = AuthorUiModel(
            id = "author1",
            username = "johndoe",
            displayName = "John Doe",
            avatar = "😐",
            hasNuksta = true,
            verified = true,
            pin = null
        )

        val samplePost = PostUiModel(
            id = "1",
            content = "This is a sample post content for the preview.",
            spans = emptyList(),
            author = sampleAuthor,
            attachments = emptyList(),
            likesCount = 10,
            isLiked = false,
            commentsCount = 5,
            repostsCount = 2,
            isReposted = true,
            viewsCount = 100,
            dominantEmoji = "🦎",
            isPinned = false,
            isOwner = false,
            originalPost = null,
            poll = null,
        )

        val sampleComments = listOf(
            CommentDto(
                id = "1",
                content = "This is a sample comment",
                author = com.dertefter.data.dto.feed.AuthorDto(
                    id = "1",
                    avatar = "😊",
                    username = "johndoe",
                    verified = true,
                    hasNuksta = false,
                    displayName = "John Doe"
                ),
                likesCount = 10,
                repliesCount = 2,
                isLiked = false,
                createdAt = "2023-10-27T10:00:00Z"
            )
        )

        val samplePaginator = mutableCursorPaginator {
            load {
                CursorLoadResult(
                    data = sampleComments,
                    bookmark = CursorBookmark(null, "initial", null)
                )
            }
        }

        val uiState = UiState(
            post = samplePost,
            isLoading = false
        )

        PostScreenContent(
            uiState = uiState,
            meUserId = "author1",
            onEvent = {},
            selectedTab = CommentSort.POPULAR,
            commentsUiState = PaginatorUiState.Content(
                prependState = null,
                items = sampleComments,
                appendState = null
            ),
            commentsPaginator = samplePaginator,
            onCommentsEvent = {}
        )
    }
}
