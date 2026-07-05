package com.dertefter.attachment_viewer.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.ScreenScaffold
import com.dertefter.design.components.post.Attachment
import com.dertefter.design.components.post.AttachmentUiModel
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable


@Composable
fun AttachmentViewerScreen(
    onEvent: (Event) -> Unit,
    attachments: List<AttachmentUiModel>,
    viewPosition: Int = 0,
) {
    val pagerState = rememberPagerState(
        initialPage = viewPosition,
        pageCount = { attachments.size }
    )

    ScreenScaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize(),
                pageSpacing = 8.dp,
                userScrollEnabled = true,
            ) { page ->
                val attachment = attachments[page]
                val zoomState = rememberZoomState()
                Attachment(
                    attachment = attachment,
                    contentScale = ContentScale.Fit,
                    containerColor = Color.Black,
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (attachment.type == "image") {
                                Modifier.zoomable(zoomState)
                            } else Modifier
                        )
                )

            }

            if (attachments.size > 1) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${attachments.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
