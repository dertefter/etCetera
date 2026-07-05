package com.dertefter.hashtag_feed.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.design.components.common.TransformingListItem
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.theme.WearableTheme
import com.dertefter.design.theme.spacing
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator

@Composable
fun HashtagFeedScreen(
    onEvent: (Event) -> Unit,
    uiState: UiState,
    paginator: MutableCursorPaginator<String, PostDto>? = null
) {
    if (paginator != null) {
        Feed(
            paginator = paginator,
            onEvent = onEvent,
            uiState = uiState.uiState,
            header = { transformationSpec ->
                item(key = "hashtag_header") {
                    TransformingListItem(transformationSpec = transformationSpec) {
                        Text(
                            text = uiState.hashtag?.let { "#$it" } ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AppLoadingIndicator()
        }
    }
}

@Preview(device = "id:wearos_large_round")
@Composable
fun HashtagFeedScreenPreview() {
    WearableTheme {
        HashtagFeedScreen(
            onEvent = {},
            uiState = UiState(
                hashtag = "test",
                isLoading = false,
                uiState = PaginatorUiState.Idle
            ),
        )
    }
}
