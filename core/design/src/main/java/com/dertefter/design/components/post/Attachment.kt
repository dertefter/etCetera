package com.dertefter.design.components.post

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Attachment(
    attachment: AttachmentUiModel,
    modifier: Modifier = Modifier,
) {
    when {
        attachment.mimeType?.startsWith("video") == true || attachment.type.startsWith("video") -> {
            VideoAttachment(
                attachment = attachment,
                modifier = modifier
            )
        }

        attachment.mimeType?.startsWith("image") == true || attachment.type.startsWith("image") -> {
            ImageAttachment(
                attachment = attachment,
                modifier = modifier
            )
        }

        else -> {
            UnsupportedAttachment(
                mimeType = attachment.mimeType,
                type = attachment.type,
                modifier = modifier
            )
        }
    }
}
