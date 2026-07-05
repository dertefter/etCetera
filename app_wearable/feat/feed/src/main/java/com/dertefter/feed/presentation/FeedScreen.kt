package com.dertefter.feed.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.design.components.common.TransformingListItem
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing
import com.dertefter.feed.R
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult

@Composable
fun FeedScreen(
    onEvent: (Event) -> Unit,
    selectedTab: FeedTab,
    uiStates: Map<FeedTab, PaginatorUiState<PostDto>>,
    paginators: Map<FeedTab, MutableCursorPaginator<String, PostDto>>,
    topAppBarState: TopBarUiState
) {
    val tabs = FeedTab.entries

    val popularListState = rememberTransformingLazyColumnState()
    val clanListState = rememberTransformingLazyColumnState()
    val followingListState = rememberTransformingLazyColumnState()

    val listStates = remember {
        mapOf(
            FeedTab.POPULAR to popularListState,
            FeedTab.CLAN to clanListState,
            FeedTab.FOLLOWING to followingListState
        )
    }

    Feed(
        paginator = paginators[selectedTab]!!,
        onEvent = onEvent,
        uiState = uiStates[selectedTab]!!,
        listState = listStates[selectedTab]!!,
        header = { transformationSpec ->
            item(key = "feed_tabs_header") {
                TransformingListItem(transformationSpec = transformationSpec) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.spacing.medium)
                            .fillMaxWidth()
                    ) {
                        tabs.forEach { tab ->
                            val text = when (tab) {
                                FeedTab.POPULAR -> stringResource(R.string.feed_popular)
                                FeedTab.CLAN -> stringResource(R.string.feed_clan)
                                FeedTab.FOLLOWING -> stringResource(R.string.feed_following)
                            }

                            val icon = when (tab) {
                                FeedTab.POPULAR -> Icons.Explore
                                FeedTab.CLAN -> null
                                FeedTab.FOLLOWING -> Icons.Group
                            }

                            val isSelected = selectedTab == tab

                            val containerColor by animateColorAsState(
                                if (isSelected) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.surfaceContainer
                            )

                            val shapeCorner by animateDpAsState(
                                if (isSelected) MaterialTheme.rounding.extraLarge else MaterialTheme.rounding.small
                            )

                            val contentColor by animateColorAsState(
                                if (isSelected) MaterialTheme.colorScheme.onSecondary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )



                            Box(
                                modifier = Modifier
                                    .padding(horizontal = MaterialTheme.spacing.extraSmall)
                                    .weight(1f)
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(shapeCorner))
                                    .clickable(
                                        onClick = {
                                            onEvent(Event.OnTabSelected(tab))
                                        }
                                    )
                                    .background(containerColor),
                                contentAlignment = Alignment.Center
                                ,
                            ){
                                icon?.let { icon ->
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = text,
                                        tint = contentColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                if (tab == FeedTab.CLAN){
                                    Text(
                                        text = topAppBarState.avatarEmoji ?: "",
                                        fontSize = 16.sp
                                    )
                                }

                            }
                        }
                    }

                    }
                }
            }
        )

}

@Composable
@Preview(device = "id:wearos_large_round")
fun FeedScreenPreview() {
    MaterialTheme {
        val sampleAuthor = AuthorDto(
            id = "1",
            avatar = "😊",
            username = "johndoe",
            verified = true,
            hasNuksta = false,
            displayName = "John Doe"
        )

        val samplePosts = listOf(
            PostDto(
                id = "1",
                content = "This is a sample post",
                spans = emptyList(),
                likesCount = 10,
                commentsCount = 2,
                repostsCount = 1,
                viewsCount = 100,
                author = sampleAuthor,
                attachments = emptyList(),
                isLiked = false,
                isReposted = false,
                isOwner = false,
                isViewed = true,
                createdAt = "2023-10-27T10:00:00Z",
                vs = ""
            )
        )

        val samplePaginator = mutableCursorPaginator {
            load {
                CursorLoadResult(
                    data = samplePosts,
                    bookmark = CursorBookmark(null, "initial", null)
                )
            }
        }
        val paginators = mapOf(
            FeedTab.POPULAR to samplePaginator,
            FeedTab.CLAN to samplePaginator,
            FeedTab.FOLLOWING to samplePaginator
        )
        val uiStates = mapOf(
            FeedTab.POPULAR to PaginatorUiState.Content(
                prependState = null,
                items = samplePosts,
                appendState = null
            ),
            FeedTab.CLAN to PaginatorUiState.Content(
                prependState = null,
                items = samplePosts,
                appendState = null
            ),
            FeedTab.FOLLOWING to PaginatorUiState.Content(
                prependState = null,
                items = samplePosts,
                appendState = null
            )
        )

        FeedScreen(
            onEvent = {},
            selectedTab = FeedTab.POPULAR,
            uiStates = uiStates,
            topAppBarState = TopBarUiState(),
            paginators = paginators,
        )
    }
}
