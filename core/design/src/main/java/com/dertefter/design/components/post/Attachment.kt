package com.dertefter.design.components.post

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Attachment(
    attachment: AttachmentUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    when {
        attachment.mimeType?.startsWith("video") == true || attachment.type.startsWith("video") -> {
            VideoAttachment(
                attachment = attachment,
                modifier = modifier
                    .clickable(onClick = onClick)
            )
        }

        attachment.mimeType?.startsWith("image") == true || attachment.type.startsWith("image") -> {
            ImageAttachment(
                attachment = attachment,
                modifier = modifier
                    .clickable(onClick = onClick)
            )
        }

        else -> {
            UnsupportedAttachment(
                mimeType = attachment.mimeType,
                type = attachment.type,
                modifier = modifier
                    .clickable(onClick = onClick)
            )
        }
    }
}
