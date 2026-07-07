package com.dertefter.notifications.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.dertefter.data.dto.notifications.ActorDto
import com.dertefter.notifications.R
import com.dertefter.data.dto.notifications.NotificationDto
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing

@Composable
fun NotificationCard(
    modifier: Modifier = Modifier,
    notification: NotificationDto,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    onUserClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {

    val largeRounding = MaterialTheme.rounding.largeIncreased
    val smallRounding = MaterialTheme.rounding.small

    val shape = RoundedCornerShape(
        topStart = if (isFirst) largeRounding else smallRounding,
        topEnd = if (isFirst) largeRounding else smallRounding,
        bottomStart = if (isLast) largeRounding else smallRounding,
        bottomEnd = if (isLast) largeRounding else smallRounding,
    )

    val topPadding = if (isFirst) MaterialTheme.spacing.small else 0.dp
    val bottomPadding = if (isLast) MaterialTheme.spacing.small else 0.dp

    Row(
        modifier = modifier
            .padding(top = topPadding, bottom = bottomPadding)
            .fillMaxWidth()
            .clip(shape)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(MaterialTheme.spacing.large),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SmallEmojiAvatar(
            emoji = notification.actor.avatar,
            containerSize = 48.dp,
            onClick = onUserClick,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier
                .align(Alignment.Top)
        )
        Column(
            modifier = Modifier.weight(1f),
        )
        {
            Text(
                text = notification.actor.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable(onClick = onUserClick)
            )
            Text(
                text = getNotificationText(notification),
                style = MaterialTheme.typography.bodyMedium
            )
            if (!notification.preview.isNullOrEmpty()){
                Text(
                    text = notification.preview!!,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.small),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
    }
}

@Composable
private fun getNotificationText(notification: NotificationDto): String {
    return when (notification.type) {
        "follow" -> stringResource(R.string.notification_type_follow)
        "like" -> stringResource(R.string.notification_type_like)
        "comment" -> stringResource(R.string.notification_type_comment)
        "wall_post" -> stringResource(R.string.notification_type_wall_post)
        "repost" -> stringResource(R.string.notification_type_repost)
        else -> stringResource(R.string.notification_type_unknown)
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
                preview = "Превтб",
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
