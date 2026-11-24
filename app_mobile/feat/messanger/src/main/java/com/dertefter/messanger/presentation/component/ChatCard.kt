package com.dertefter.messanger.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.messenger.ChatDto
import com.dertefter.data.dto.messenger.LastMessageDto
import com.dertefter.design.components.avatar.Avatar
import com.dertefter.design.components.lists.SegmentedContentItem
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@Composable
fun ChatCard(
    modifier: Modifier = Modifier,
    chat: ChatDto,
    index: Int = 0,
    count: Int = 1,
    onClick: () -> Unit = {},
) {
    SegmentedContentItem(
        modifier = modifier.fillMaxWidth(),
        index = index,
        count = count,
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                data = chat.avatar ?: "💬"
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
            ) {
                Text(
                    text = chat.title ?: "Chat",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val lastMsgText = chat.lastMessage?.text
                if (!lastMsgText.isNullOrEmpty()) {
                    Text(
                        text = lastMsgText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (chat.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (chat.unreadCount > 99) "99+" else chat.unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatCardPreview() {
    AppTheme {
        ChatCard(
            chat = ChatDto(
                chatId = "1",
                title = "Кто тут?",
                avatar = "👾",
                unreadCount = 3,
                lastMessage = LastMessageDto(
                    id = "msg1",
                    text = "Привет! Как дела?"
                )
            )
        )
    }
}
