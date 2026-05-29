package com.dertefter.followers.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.design.components.avatar.SmallEmojiAvatar
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
            .clickable { onClick() }
            .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding, vertical = MaterialTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
    ) {
        SmallEmojiAvatar(
            emoji = followerUser.avatar,
            containerSize = 40.dp
        )
        Column {
            Text(
                text = followerUser.displayName,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "@${followerUser.username}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.weight(1f))
        if (followerUser.isFollowing) {
            OutlinedButton(onClick = { onUnfollow(followerUser.id) }) {
                Text("Unfollow")
            }
        } else {
            Button(onClick = { onFollow(followerUser.id) }) {
                Text("Follow")
            }
        }
    }
}
