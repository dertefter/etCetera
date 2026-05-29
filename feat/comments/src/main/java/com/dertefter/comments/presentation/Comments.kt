package com.dertefter.comments.presentation

import android.widget.Space
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.comments.R
import com.dertefter.comments.presentation.component.CommentCard
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.gigamole.composefadingedges.FadingEdgesGravity
import com.gigamole.composefadingedges.verticalFadingEdges
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.bookmark.CursorBookmark
import com.jamal_aliev.paginator.compose.paginated
import com.jamal_aliev.paginator.compose.rememberPaginated
import com.jamal_aliev.paginator.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.extension.isErrorState
import com.jamal_aliev.paginator.extension.isProgressState
import com.jamal_aliev.paginator.load.CursorLoadResult
import com.jamal_aliev.paginator.page.PaginatorUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Comments(
    postId: String,
    paginator: MutableCursorPaginator<CommentDto>,
    selectedTab: CommentSort,
    onEvent: (Event) -> Unit,
    uiState: PaginatorUiState<CommentDto>,
    contentPadding: PaddingValues = PaddingValues(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val listState = rememberLazyListState()
    val paged = paginator.rememberPaginated(state = listState)



    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        },
        label = "feed_state",
        modifier = Modifier
            .fillMaxSize()
            .verticalFadingEdges(
                gravity = FadingEdgesGravity.Start,
                length = contentPadding.calculateTopPadding()
            )
            .verticalFadingEdges(
                gravity = FadingEdgesGravity.End,
                length = contentPadding.calculateBottomPadding() + MaterialTheme.spacing.extraLarge
            )
    ){ state ->
        when (state) {
            PaginatorUiState.Idle -> {
                Box(
                    Modifier
                        .padding(MaterialTheme.spacing.large)
                        .padding(contentPadding)
                        .fillMaxSize(), contentAlignment = Alignment.TopCenter
                ) {
                    AppLoadingIndicator()
                }
            }

            is PaginatorUiState.Loading -> {
                Box(
                    Modifier
                        .padding(MaterialTheme.spacing.large)
                        .padding(contentPadding)
                        .fillMaxSize(), contentAlignment = Alignment.TopCenter
                ) {
                    AppLoadingIndicator()
                }
            }

            is PaginatorUiState.Empty -> {
                Box(
                    Modifier
                        .padding(MaterialTheme.spacing.large)
                        .padding(contentPadding)
                        .fillMaxSize(), contentAlignment = Alignment.TopCenter
                ) {
                    Text(stringResource(R.string.comments_empty))
                }
            }

            is PaginatorUiState.Error -> {
                Box(
                    Modifier
                        .padding(MaterialTheme.spacing.large)
                        .padding(contentPadding)
                        .fillMaxSize(), contentAlignment = Alignment.TopCenter
                ) {
                    Text(stringResource(R.string.comments_failed_to_load))
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
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
                    state = listState,
                    contentPadding = contentPadding
                ) {

                    item {
                        Spacer(
                            modifier = Modifier.height(MaterialTheme.spacing.small)
                        )
                    }

                    paginated(paged) {
                        itemsIndexed(
                            state.items,
                            key = { _, comment -> comment.id }) { index, comment ->
                            CommentCard(
                                comment = comment,
                                onLike = { onEvent(Event.OnLike(it)) },
                                onUnlike = { onEvent(Event.OnUnlike(it)) },
                                onLoadMoreReplies = { onEvent(Event.OnLoadMoreReplies(it)) },
                            )
                            if (index < state.items.lastIndex) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
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
                                        AppLoadingIndicator()
                                    } else if (appendState.isErrorState()) {
                                        Text(stringResource(R.string.comments_failed_to_load))
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

@Preview(showBackground = true)
@Composable
fun CommentsPreview() {
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
                content = "Another sample comment with some more text to see how it looks when it wraps to multiple lines in the preview.",
                author = sampleAuthor,
                likesCount = 5,
                repliesCount = 0,
                isLiked = true,
                createdAt = "2023-10-27T11:00:00Z"
            )
        )

        val samplePaginator = mutableCursorPaginator<CommentDto> {
            load {
                CursorLoadResult(
                    data = sampleComments,
                    bookmark = CursorBookmark(null, "initial", null)
                )
            }
        }

        val uiState = PaginatorUiState.Content(
            prependState = null,
            items = sampleComments,
            appendState = null
        )

        Comments(
            postId = "1",
            paginator = samplePaginator,
            selectedTab = CommentSort.POPULAR,
            onEvent = {},
            uiState = uiState
        )
    }
}
