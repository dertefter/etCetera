package com.dertefter.design.components.post

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.dertefter.design.theme.AppTheme


enum class AttachmentContentScale{
    FIT, CROP
}

@Composable
@OptIn(UnstableApi::class)
fun Attachment(
    attachment: AttachmentUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isFullscreen: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(),
    contentScale: AttachmentContentScale? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {

    when {
        attachment.mimeType?.startsWith("video") == true || attachment.type.startsWith("video") -> {
            VideoAttachment(
                modifier = modifier,
                attachment = attachment,
                isFullscreen = isFullscreen,
                onClick = onClick,
                contentPadding = contentPadding,
                contentScale = contentScale,
            )
        }

        attachment.mimeType?.startsWith("image") == true || attachment.type.startsWith("image") -> {
            ImageAttachment(
                modifier = modifier,
                attachment = attachment,
                containerColor = containerColor,
                isFullscreen = isFullscreen,
                onClick = onClick,
                contentScale = contentScale,
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

@Preview(showBackground = false)
@Composable
fun AttachmentImagePreview() {
    AppTheme(
        content = {
            Attachment(
                attachment = AttachmentUiModel(
                    id = "1",
                    type = "image",
                    url = "https://picsum.photos/400/300",
                    mimeType = "image/jpeg"
                ),
                modifier = Modifier.size(200.dp),
            )
        },
    )
}

@Preview(showBackground = false)
@Composable
fun AttachmentVideoPreview() {
    AppTheme(
        content = {
            Attachment(
                attachment = AttachmentUiModel(
                    id = "2",
                    type = "video",
                    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    mimeType = "video/mp4"
                ),
                modifier = Modifier.size(200.dp),
            )
        },
    )
}

@Preview(showBackground = false)
@Composable
fun AttachmentUnsupportedPreview() {
    AppTheme(
        content = {
            Attachment(
                attachment = AttachmentUiModel(
                    id = "3",
                    type = "file",
                    url = "https://example.com/document.pdf",
                    mimeType = "application/pdf"
                ),
                modifier = Modifier.size(200.dp),
            )
        },
    )
}
