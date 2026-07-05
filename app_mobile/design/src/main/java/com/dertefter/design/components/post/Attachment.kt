

package com.dertefter.design.components.post

import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.media3.common.util.UnstableApi

@Composable
@OptIn(UnstableApi::class)
fun Attachment(
    attachment: AttachmentUiModel,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    onClick: () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    when {
        attachment.mimeType?.startsWith("video") == true || attachment.type.startsWith("video") -> {
            VideoAttachment(
                attachment = attachment,
                contentScale = contentScale,
                containerColor = containerColor,
                modifier = modifier
                    .clickable(onClick = onClick)
            )
        }

        attachment.mimeType?.startsWith("image") == true || attachment.type.startsWith("image") -> {
            ImageAttachment(
                attachment = attachment,
                contentScale = contentScale,
                containerColor = containerColor,
                modifier = modifier
                    .clickable(onClick = onClick)
            )
        }

        else -> {
            UnsupportedAttachment(
                mimeType = attachment.mimeType,
                type = attachment.type,
                containerColor = containerColor,
                modifier = modifier
                    .clickable(onClick = onClick)
            )
        }
    }
}
