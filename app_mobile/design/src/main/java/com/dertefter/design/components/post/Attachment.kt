

package com.dertefter.design.components.post

import androidx.annotation.OptIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.util.UnstableApi

@Composable
@OptIn(UnstableApi::class)
fun Attachment(
    attachment: AttachmentUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isFullscreen: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {

    when {
        attachment.mimeType?.startsWith("video") == true || attachment.type.startsWith("video") -> {
            VideoAttachment(
                modifier = modifier,
                attachment = attachment,
                isFullscreen = isFullscreen,
                onClick = onClick
            )
        }

        attachment.mimeType?.startsWith("image") == true || attachment.type.startsWith("image") -> {
            ImageAttachment(
                modifier = modifier,
                attachment = attachment,
                containerColor = containerColor,
                isFullscreen = isFullscreen,
                onClick = onClick,
            )
        }

        else -> {
            UnsupportedAttachment(
                modifier = modifier,
                mimeType = attachment.mimeType,
                type = attachment.type,
                containerColor = containerColor,
                onClick = onClick
            )
        }
    }
}
