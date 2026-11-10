package com.dertefter.post.presentation

import android.content.ClipData
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.comments.CommentsViewModel
import com.dertefter.comments.presentation.CommentSort
import com.dertefter.comments.presentation.CommentsFeed
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.appbar.AppTopBarStyle
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.post.AuthorUiModel
import com.dertefter.design.components.post.PostCardExtended
import com.dertefter.design.components.post.PostUiModel
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.spacing
import com.dertefter.post.R
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch
import com.dertefter.comments.presentation.Event as CommentsEvent

@Composable
fun PostScreen(
    uiState: UiState,
    meUserId: String?,
    onEvent: (Event) -> Unit,
    commentsViewModel: CommentsViewModel = metroViewModel()
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
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()
    var showCommentsFiltersMenu by remember { mutableStateOf(false) }

    var showMenu by remember { mutableStateOf(false) }

    var pullRefreshing by remember { mutableStateOf(false) }

    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

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
                    appTopBarStyle =  AppTopBarStyle.SMALL,
                    hazeState = hazeState,
                    title = {},
                    navigationIcon = {
                        AppNavigationIcon(
                            onClick = { onEvent(Event.OnNavigateBack) }
                        )
                    },
                    actions = {

                        uiState.post?.let { post ->
                            Box {
                                AppNavigationIcon(
                                    onClick = { showMenu = true },
                                    icon = Icons.MoreVert
                                )

                                DropdownMenu(
                                    expanded = showMenu,
                                    shape = MaterialTheme.shapes.largeIncreased,
                                    onDismissRequest = { showMenu = false }) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(com.dertefter.design.R.string.design_post_copy_link)) },
                                        onClick = {
                                            val link =
                                                "https://итд.com/@${post.author.username}/post/${post.id}"
                                            scope.launch {
                                                clipboard.setClipEntry(
                                                    ClipEntry(
                                                        ClipData.newPlainText(
                                                            null, link
                                                        )
                                                    )
                                                )
                                            }
                                            showMenu = false
                                        },
                                        leadingIcon = {
                                            Icon(Icons.ContentCopy, contentDescription = null)
                                        })
                                    if (post.canEdit()) {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(com.dertefter.design.R.string.design_post_edit)) },
                                            onClick = {
                                                onEvent(
                                                    Event.OnEditPost(postId = post.id)
                                                )
                                                showMenu = false
                                            },
                                            leadingIcon = {
                                                Icon(Icons.Edit, contentDescription = null)
                                            })
                                    }
                                    if (post.isOwner) {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(com.dertefter.design.R.string.design_post_delete)) },
                                            onClick = {
                                                onEvent(
                                                    Event.OnDeletePost(postId = post.id)
                                                )
                                                showMenu = false
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Delete,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            })
                                    }
                                    if (!post.isOwner){
                                        DropdownMenuItem(
                                            text = { Text(stringResource(com.dertefter.design.R.string.design_comment_report)) },
                                            onClick = {
                                                onEvent(
                                                    Event.OnReport(targetId = post.id, targetType = "post")
                                                )
                                                showMenu = false
                                                showMenu = false
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Error,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            })
                                    }
                                }
                            }
                        }


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
                            contentPadding = contentPadding
                                    + PaddingValues(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                                    + PaddingValues(bottom = MaterialTheme.bottomNavHeight),
                            scrollBehavior = scrollBehavior,
                            header = {
                                item(key = "post_card_${post.id}") {
                                    PostCardExtended(
                                        post = post,
                                        onRepostClick = { postId -> onEvent(Event.OnRepost(postId)) },
                                        onLike = { postId -> onEvent(Event.OnLike(postId)) },
                                        onUnlike = { postId -> onEvent(Event.OnUnlike(postId)) },
                                        onCommentsClick = { postId -> onEvent(Event.OnNavigateToComments(postId)) },
                                        onUserClick = { userId -> onEvent(Event.OnOpenUser(userId)) },
                                        onVote = { postId, optionIds -> onEvent(Event.OnVote(postId, optionIds)) },
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
                                    )
                                }
                                item {
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = MaterialTheme.spacing.small)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {

                                        Box {
                                            AppNavigationIcon(
                                                onClick = { showCommentsFiltersMenu = true },
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                icon = Icons.SwapVert,
                                                contentDescription = stringResource(com.dertefter.comments.R.string.comments_sort)
                                            )
                                            DropdownMenu(
                                                expanded = showCommentsFiltersMenu,
                                                shape = MaterialTheme.shapes.large,
                                                onDismissRequest = { showCommentsFiltersMenu = false }
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
                                                            showCommentsFiltersMenu = false
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

@Preview(showBackground = true)
@Composable
fun PostScreenContentPreview() {
    AppTheme {
        val sampleAuthor = AuthorDto(
            id = "1",
            avatar = "😊",
            username = "johndoe",
            verified = true,
            hasNuksta = false,
            displayName = "John Doe"
        )

        val sampleComments = listOf(
            CommentDto(
                id = "1",
                content = "This is a sample comment",
                author = sampleAuthor,
                likesCount = 10,
                repliesCount = 2,
                isLiked = false,
                createdAt = "2023-10-27T10:00:00Z"
            ),
            CommentDto(
                id = "2",
                content = "Another sample comment with some more text to see how it looks.",
                author = sampleAuthor,
                likesCount = 5,
                repliesCount = 0,
                isLiked = true,
                createdAt = "2023-10-27T11:00:00Z"
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

        val samplePost = PostUiModel(
            id = "1",
            content = "This is a preview post content #sample",
            spans = emptyList(),
            author = AuthorUiModel(
                id = "author1",
                username = "johndoe",
                displayName = "John Doe",
                avatar = "😐",
                hasNuksta = false,
                verified = true,
                pin = null
            ),
            attachments = emptyList(),
            poll = null,
            likesCount = 10,
            isLiked = false,
            commentsCount = 2,
            repostsCount = 1,
            isReposted = false,
            isViewed = true,
            viewsCount = 42,
            dominantEmoji = null,
            isPinned = false,
            isOwner = false,
            createdAt = "2026-08-05T12:00:00Z",
            editedAt = null,
            originalPost = null
        )

        PostScreenContent(
            uiState = UiState(post = samplePost),
            meUserId = "author1",
            onEvent = {},
            commentSort = CommentSort.POPULAR,
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


