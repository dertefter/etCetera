package com.dertefter.comments.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.comments.R
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CommentsScreen(
    meUserId: String? = null,
    onEvent: (Event) -> Unit,
    selectedTab: CommentSort,
    uiStates: Map<CommentSort, PaginatorUiState<CommentDto>>,
    paginators: Map<CommentSort, MutableCursorPaginator<String, CommentDto>>
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showMenu by remember { mutableStateOf(false) }
    val sorts = CommentSort.entries

    val alpha by animateFloatAsState(
        targetValue = if (scrollBehavior.state.contentOffset < 0f) 0f else 1f
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                modifier = Modifier
                    .padding(bottom = MaterialTheme.spacing.defaultScreenPadding)
                    .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
            ){


                Box {
                    AppNavigationIcon(
                        onClick = { showMenu = true },
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        icon = Icons.SwapVert,
                        contentDescription = stringResource(R.string.comments_sort)
                    )
                    DropdownMenu(
                        expanded = showMenu,
                        shape = MaterialTheme.shapes.large,
                        onDismissRequest = { showMenu = false }
                    ) {
                        sorts.forEach { tab ->

                            val text = when(tab){
                                CommentSort.POPULAR -> stringResource(R.string.comments_popular)
                                CommentSort.OLDEST -> stringResource(R.string.comments_oldest)
                                CommentSort.NEWEST ->  stringResource(R.string.comments_newest)
                            }

                            DropdownMenuItem(
                                text = { Text(text) },
                                onClick = {
                                    onEvent(Event.OnTabSelected(tab))
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
                    modifier = Modifier
                        .alpha(alpha)
                        .weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    text = stringResource(R.string.comments_title),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                AppNavigationIcon(
                    onClick = {
                        onEvent(Event.OnNewComment)
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    icon = Icons.Add,
                    contentDescription = "New comment"
                )

            }
        }
    ) { contentPadding ->
        key(selectedTab) {
            Comments(
                meUserId = meUserId,
                paginator = paginators[selectedTab]!!,
                onEvent = onEvent,
                uiState = uiStates[selectedTab]!!,
                contentPadding = contentPadding,
                scrollBehavior = scrollBehavior
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun CommentsScreenPreview() {
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

        val samplePaginator = mutableCursorPaginator {
            load {
                CursorLoadResult(
                    data = sampleComments,
                    bookmark = CursorBookmark(null, "initial", null)
                )
            }
        }
        val paginators = mapOf(
            CommentSort.POPULAR to samplePaginator,
            CommentSort.NEWEST to samplePaginator
        )
        val uiStates = mapOf(
            CommentSort.POPULAR to PaginatorUiState.Content(
                prependState = null,
                items = sampleComments,
                appendState = null
            ),
            CommentSort.NEWEST to PaginatorUiState.Content(
                prependState = null,
                items = sampleComments,
                appendState = null
            )
        )

        CommentsScreen(
            onEvent = {},
            selectedTab = CommentSort.POPULAR,
            uiStates = uiStates,
            paginators = paginators
        )
    }
}
