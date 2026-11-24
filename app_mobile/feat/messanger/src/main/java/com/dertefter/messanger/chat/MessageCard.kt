package com.dertefter.messanger.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.messenger.MessageDto
import com.dertefter.design.components.post.Attachment
import com.dertefter.design.components.post.AttachmentUiModel
import com.dertefter.design.components.post.AttachmentsCarousel
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@Composable
fun MessageCard(
    modifier: Modifier = Modifier,
    message: MessageDto,
    meUserId: String? = null,
    onAttachmentClick: (attachments: List<AttachmentUiModel>, position: Int) -> Unit = { _, _ -> }
) {
    val isMe = message.senderId == meUserId

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.extraSmall)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
            contentColor = if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(if (isMe) Alignment.CenterEnd else Alignment.CenterStart)
                .widthIn(max = 290.dp)
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                message.text?.let { text ->
                    if (text.isNotEmpty()) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (message.attachments.isNotEmpty()) {
                    val uiAttachments = message.attachments.map { att ->
                        val extension = att.filename?.substringAfterLast('.', "")?.lowercase() ?: ""
                        val isImage = att.mime?.startsWith("image") == true || extension in listOf("jpg", "jpeg", "png", "webp", "gif", "heic")
                        val isVideo = att.mime?.startsWith("video") == true || extension in listOf("mp4", "mov", "webm", "mkv", "avi")

                        val inferredMime = when {
                            isImage -> att.mime?.takeIf { it.startsWith("image") } ?: "image/jpeg"
                            isVideo -> att.mime?.takeIf { it.startsWith("video") } ?: "video/mp4"
                            else -> "unsupported"
                        }

                        AttachmentUiModel(
                            id = att.fileId ?: "",
                            type = inferredMime,
                            url = att.url,
                            mimeType = inferredMime
                        )
                    }

                    uiAttachments.forEachIndexed { index, attachment ->
                        if (attachment.type == "unsupported") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "вложение не поддерживается",
                                    style = MaterialTheme.typography.labelLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            if (uiAttachments.size == 1) {
                                Attachment(
                                    attachment = attachment,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(MaterialTheme.shapes.medium),
                                    onClick = {
                                        onAttachmentClick(uiAttachments, index)
                                    }
                                )
                            } else {
                                AttachmentsCarousel(
                                    attachments = uiAttachments.filter { it.type != "unsupported" },
                                    modifier = Modifier.fillMaxWidth(),
                                    itemHeight = 200.dp,
                                    itemWidth = 200.dp,
                                    onItemClick = { position ->
                                        onAttachmentClick(uiAttachments, position)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageCardPreview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MessageCard(
                message = MessageDto(
                    messageId = "1",
                    senderId = "user1",
                    text = "Hello! This is a message from someone else."
                ),
                meUserId = "me"
            )

            MessageCard(
                message = MessageDto(
                    messageId = "1",
                    senderId = "user1",
                    text = "Hello! This is a meffffffffffffffffffffffffffffffffffffffffffffffffffffffffff ff fff fff ff ff f f fffff ssage from someone else."
                ),
                meUserId = "me"
            )

            MessageCard(
                message = MessageDto(
                    messageId = "2",
                    senderId = "me",
                    text = "Hi there! This is my message."
                ),
                meUserId = "me"
            )
        }
    }
}
