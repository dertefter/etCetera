package com.dertefter.design.components.post

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentsCarousel(
    attachments: List<AttachmentUiModel>,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 260.dp,
    itemShape: CornerBasedShape = MaterialTheme.shapes.large,
    contentPadding: PaddingValues = PaddingValues(horizontal = MaterialTheme.spacing.defaultScreenPadding),
    onItemClick: (position: Int) -> Unit  = {}
) {
    if (attachments.isEmpty()) return

    val state = rememberCarouselState { attachments.size }
    HorizontalMultiBrowseCarousel(
        state = state,
        modifier = modifier
            .padding(contentPadding)
            .fillMaxWidth(),
        itemSpacing = MaterialTheme.spacing.medium,
        preferredItemWidth = 260.dp,
    ) { index ->
        Attachment(
            attachment = attachments[index],
            modifier = Modifier
                .height(itemHeight)
                .maskClip(itemShape),
            onClick = {
                onItemClick(index)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AttachmentsCarouselPreview() {
    AppTheme {
        AttachmentsCarousel(
            attachments = listOf(
                AttachmentUiModel(
                    id = "1",
                    type = "image",
                    url = "https://picsum.photos/400/300",
                    mimeType = "image/jpeg"
                ),
                AttachmentUiModel(
                    id = "1",
                    type = "image",
                    url = "https://picsum.photos/400/300",
                    mimeType = "image/jpeg"
                ),
                AttachmentUiModel(
                    id = "1",
                    type = "image",
                    url = "https://picsum.photos/400/300",
                    mimeType = "image/jpeg"
                ),
                AttachmentUiModel(
                    id = "2",
                    type = "video",
                    url = "https://example.com/video.mp4",
                    mimeType = "video/mp4"
                ),
                AttachmentUiModel(
                    id = "3",
                    type = "unknown",
                    url = null,
                    mimeType = "application/pdf"
                )
            )
        )
    }
}
