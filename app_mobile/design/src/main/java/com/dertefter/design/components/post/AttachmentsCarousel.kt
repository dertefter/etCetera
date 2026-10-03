package com.dertefter.design.components.post

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dertefter.design.theme.attachmentsIsCarousel
import com.dertefter.design.theme.spacing

@Composable
fun AttachmentsCarousel(
    attachments: List<AttachmentUiModel>,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 320.dp,
    itemWidth: Dp = 260.dp,
    itemShape: CornerBasedShape = MaterialTheme.shapes.largeIncreased,
    contentPadding: PaddingValues = PaddingValues(),
    onItemClick: (position: Int) -> Unit = {},
) {
    if (attachments.isEmpty()) return

    when (MaterialTheme.attachmentsIsCarousel) {
        false -> {
            val state = rememberCarouselState { attachments.size }
            HorizontalUncontainedCarousel(
                state = state,
                modifier = modifier.fillMaxWidth(),
                itemSpacing = MaterialTheme.spacing.small,
                itemWidth = itemWidth,
                contentPadding = contentPadding
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
        true -> {
            val state = rememberCarouselState { attachments.size }
            HorizontalMultiBrowseCarousel(
                state = state,
                modifier = modifier
                    .padding(contentPadding)
                    .fillMaxWidth(),
                itemSpacing = MaterialTheme.spacing.small,
                preferredItemWidth = itemWidth,
                maxSmallItemWidth = itemWidth/12,
                minSmallItemWidth = itemWidth/16
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
    }
}