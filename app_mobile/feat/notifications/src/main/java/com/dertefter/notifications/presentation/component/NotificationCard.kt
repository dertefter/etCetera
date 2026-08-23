package com.dertefter.notifications.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.notifications.ActorDto
import com.dertefter.data.dto.notifications.NotificationDto
import com.dertefter.design.components.avatar.EmojiAvatar
import com.dertefter.design.components.lists.SegmentedContentItem
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.notifications.R
import com.materialkolor.ktx.harmonize

@Composable
fun NotificationCard(
    modifier: Modifier = Modifier,
    notification: NotificationDto,
    index: Int = 1,
    count: Int = 1,
    onUserClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {

    SegmentedContentItem(
        modifier = modifier
            .fillMaxWidth(),
        index = index,
        count = count,
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Box(
                modifier = Modifier
                    .align(Alignment.Top)
            ) {

                val notificationSourceColor = getNotificationBadgeColor(notification.type)

                val notificationBadgeBgColor = notificationSourceColor.harmonize(
                    MaterialTheme.colorScheme.tertiaryContainer, true
                )

                val notificationBadgeIconColor = notificationSourceColor.harmonize(
                    MaterialTheme.colorScheme.onTertiaryContainer, true
                )

                EmojiAvatar(
                    emoji = notification.actor.avatar,
                    onClick = onUserClick,
                    modifier = Modifier
                        .padding(
                            bottom = MaterialTheme.spacing.medium,
                            end = MaterialTheme.spacing.medium
                        )
                )
                Icon(
                    imageVector = getNotificationIcon(notification.type),
                    contentDescription = null,
                    modifier = Modifier
                        .border(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            width = MaterialTheme.spacing.small
                        )
                        .padding(MaterialTheme.spacing.small)
                        .background(notificationBadgeBgColor, CircleShape)
                        .padding(MaterialTheme.spacing.small)
                        .size(16.dp)
                        .align(Alignment.BottomEnd),
                    tint = notificationBadgeIconColor
                )
            }
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


}

@Composable
private fun getNotificationText(notification: NotificationDto): String {
    val count = notification.count
    return when (notification.type) {
        "follow" -> if (count > 1) {
            pluralStringResource(R.plurals.notification_follow_multiple, count - 1, count - 1)
        } else {
            stringResource(R.string.notification_follow_single)
        }

        "follow_request" -> stringResource(R.string.notification_follow_request)
        "follow_accepted" -> stringResource(R.string.notification_follow_accepted)

        "like", "post_reaction" -> if (count > 1) {
            pluralStringResource(R.plurals.notification_post_reaction_multiple, count - 1, count - 1)
        } else {
            stringResource(R.string.notification_post_reaction_single)
        }

        "comment", "post_comment" -> stringResource(R.string.notification_post_comment)

        "repost", "post_repost" -> if (count > 1) {
            pluralStringResource(R.plurals.notification_post_repost_multiple, count - 1, count - 1)
        } else {
            stringResource(R.string.notification_post_repost_single)
        }

        "comment_reaction", "comment_like" -> if (count > 1) {
            pluralStringResource(R.plurals.notification_comment_reaction_multiple, count - 1, count - 1)
        } else {
            stringResource(R.string.notification_comment_reaction_single)
        }

        "comment_reply", "reply" -> stringResource(R.string.notification_comment_reply)
        "post_mention" -> stringResource(R.string.notification_post_mention)
        "comment_mention" -> stringResource(R.string.notification_comment_mention)
        "wall_post" -> stringResource(R.string.notification_wall_post)
        else -> stringResource(R.string.notification_type_unknown)
    }
}

@Composable
private fun getNotificationBadgeColor(type: String): Color {
    return when (type) {
        "follow", "follow_request", "repost", "post_repost", "wall_post" -> Color(0xFF2196F3) // blue
        "follow_accepted", "comment", "post_comment", "comment_reply", "reply" -> Color(0xFF4CAF50) // green
        "like", "post_reaction", "comment_reaction", "comment_like" -> Color(0xFFF44336) // red
        "post_mention", "comment_mention" -> Color(0xFF9C27B0) // purple
        else -> MaterialTheme.colorScheme.outline
    }
}

@Composable
private fun getNotificationIcon(type: String): ImageVector {
    return when (type) {
        "follow", "follow_request" -> Icons.AddGroupFilled
        "follow_accepted" -> Icons.Check
        "like", "post_reaction", "comment_reaction", "comment_like" -> Icons.FavFilled
        "comment", "post_comment", "comment_reply", "reply" -> Icons.CommentFilled
        "repost", "post_repost" -> Icons.Cached
        "post_mention", "comment_mention" -> Icons.UserFilled
        "wall_post" -> Icons.EditFilled
        else -> Icons.UserFilled
    }
}

@Preview(showBackground = false)
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
