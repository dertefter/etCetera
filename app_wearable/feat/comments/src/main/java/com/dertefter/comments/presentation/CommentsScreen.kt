package com.dertefter.comments.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.comments.R
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.design.theme.WearableTheme
import com.dertefter.design.theme.spacing
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult

@Composable
fun CommentsScreen(
    postId: String,
    meUserId: String? = null,
    onEvent: (Event) -> Unit,
    uiState: PaginatorUiState<CommentDto>,
    paginator: MutableCursorPaginator<String, CommentDto>
) {
    val listState = remember(postId) {
        TransformingLazyColumnState()
    }

    LaunchedEffect(postId) {
        listState.scrollToItem(0)
    }

    CommentsFeed(
        meUserId = meUserId,
        paginator = paginator,
        onEvent = onEvent,
        uiState = uiState,
        listState = listState,
        header = { _ ->
            item {
                Text(
                    text = stringResource(R.string.comments_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.large)
                )
            }
        }
    )
}

@Preview(device = "id:wearos_large_round", showBackground = true)
@Composable
fun CommentsScreenPreview() {
    WearableTheme {
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

        val samplePaginator = mutableCursorPaginator {
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

        CommentsScreen(
            postId = "1",
            onEvent = {},
            uiState = uiState,
            paginator = samplePaginator
        )
    }
}
