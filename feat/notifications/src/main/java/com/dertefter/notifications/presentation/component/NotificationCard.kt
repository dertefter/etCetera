package com.dertefter.notifications.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.notifications.ActorDto
import com.dertefter.data.dto.notifications.NotificationDto
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.cornerShape
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing

@Composable
fun NotificationCard(

    modifier: Modifier = Modifier,
    notification: NotificationDto,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    onClick: () -> Unit = {},
) {

    val largeRounding = MaterialTheme.rounding.largeIncreased
    val smallRounding = MaterialTheme.rounding.small

    val shape = MaterialTheme.cornerShape(
        topStart = if (isFirst) largeRounding else smallRounding,
        topEnd = if (isFirst) largeRounding else smallRounding,
        bottomStart = if (isLast) largeRounding else smallRounding,
        bottomEnd = if (isLast) largeRounding else smallRounding,
    )

    val topPadding = if (isFirst) MaterialTheme.spacing.medium else 0.dp
    val bottomPadding = if (isLast) MaterialTheme.spacing.medium else 0.dp

    Row(
        modifier = modifier
            .padding(top = topPadding, bottom = bottomPadding)
            .fillMaxWidth()
            .clickable { onClick() }
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(MaterialTheme.spacing.large),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SmallEmojiAvatar(
            emoji = notification.actor.avatar,
            containerSize = 48.dp,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.actor.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = getNotificationText(notification),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun getNotificationText(notification: NotificationDto): String {
    return when (notification.type) {
        "follow" -> "подписался на вас"
        "like" -> "поставил лайк вашему посту"
        "comment" -> "оставил комментарий"
        "wall_post" -> "опубликовал новый пост"
        else -> "новое уведомление"
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationCardPreview() {
    AppTheme {
        NotificationCard(
            notification = NotificationDto(
                id = "1",
                type = "follow",
                targetType = null,
                targetId = null,
                preview = null,
                readAt = null,
                createdAt = "2023-10-27T10:00:00Z",
                read = false,
                actor = ActorDto(
                    id = "1",
                    displayName = "Иван Иванов",
                    username = "ivanov",
                    avatar = "👋",
                    isFollowing = false,
                    isFollowedBy = false
                )
            ),
            onClick = {},
        )
    }
}
