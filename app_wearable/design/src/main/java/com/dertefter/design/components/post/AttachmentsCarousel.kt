package com.dertefter.design.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.wear.compose.material3.MaterialTheme
import com.dertefter.design.theme.WearableTheme
import com.dertefter.design.theme.spacing

@Composable
fun AttachmentsCarousel(
    attachments: List<AttachmentUiModel>,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 100.dp,
    itemShape: CornerBasedShape = MaterialTheme.shapes.medium,
    contentPadding: PaddingValues = PaddingValues(),
    onItemClick: (position: Int) -> Unit  = {}
) {
    if (attachments.isEmpty()) return

    LazyRow(
        modifier = modifier
            .clip(itemShape)
            .fillMaxWidth(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        itemsIndexed(attachments) { index, attachment ->
            Attachment(
                attachment = attachment,
                modifier = Modifier
                    .height(itemHeight)
                    .width(100.dp)
                    .clip(itemShape),
                onClick = {
                    onItemClick(index)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AttachmentsCarouselPreview() {
    WearableTheme {
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
