package com.dertefter.followers.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.theme.WearableTheme
import com.dertefter.design.theme.spacing

@Composable
fun FollowerUserCard(
    followerUser: FollowerUserDto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onFollow: (userId: String) -> Unit = {},
    onUnfollow: (userId: String) -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        SmallEmojiAvatar(
            emoji = followerUser.avatar,
            containerSize = 32.dp,
            fontSize = 14.sp
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            DisplayName(
                name = followerUser.displayName,
                verified = followerUser.verified,
                hasNuksta = false,
                pin = null,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "@${followerUser.username}",
                style = MaterialTheme.typography.bodyExtraSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(device = "id:wearos_large_round")
@Composable
fun FollowerUserCardPreview() {
    WearableTheme {
        FollowerUserCard(
            followerUser = FollowerUserDto(
                id = "1",
                username = "johndoe",
                displayName = "John Doe",
                avatar = "👤",
                verified = true,
                isFollowing = false
            )
        )
    }
}
