package com.dertefter.followers.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.avatar.EmojiAvatar
import com.dertefter.design.components.lists.SegmentedContentItem
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.followers.R

@Composable
fun FollowerUserCard(
    followerUser: FollowerUserDto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onFollow: (userId: String) -> Unit = {},
    onUnfollow: (userId: String) -> Unit = {},
    index: Int = 0,
    count: Int = 0
) {

    SegmentedContentItem(
        index = index,
        count = count
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick() },
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        )
        {
            EmojiAvatar(
                emoji = followerUser.avatar,
                containerSize = 48.dp,
                onClick = onClick
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                DisplayName(
                    name = followerUser.displayName,
                    verified = followerUser.verified,
                    hasNuksta = false,
                    pin = null
                )
                Text(
                    text = "@${followerUser.username}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (followerUser.isFollowing) {
                OutlinedButton(
                    onClick = { onUnfollow(followerUser.id) },
                ) {
                    Text(stringResource(R.string.followers_unfollow))
                }
            } else {
                Button(
                    onClick = { onFollow(followerUser.id) }
                ) {
                    Text(stringResource(R.string.followers_follow))
                }
            }
        }
    }


}

@Composable
fun FollowerUserCardVertical(
    followerUser: FollowerUserDto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onFollow: (userId: String) -> Unit = {},
    onUnfollow: (userId: String) -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        EmojiAvatar(
            emoji = followerUser.avatar,
            containerSize = 86.dp,
            fontSize = 32.sp,
            onClick = onClick
        )

        DisplayName(
            name = followerUser.displayName,
            verified = followerUser.verified,
            hasNuksta = false,
            pin = null,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.large)
        )

        if (followerUser.isFollowing) {
            OutlinedButton(
                onClick = { onUnfollow(followerUser.id) },
            ) {
                Text(stringResource(R.string.followers_unfollow))
            }
        } else {
            Button(
                onClick = { onFollow(followerUser.id) }
            ) {
                Text(stringResource(R.string.followers_follow))
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun FollowerUserCardPreview() {
    AppTheme {
        FollowerUserCardVertical(
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

@Preview(showBackground = false)
@Composable
fun FollowerUserCardFollowingPreview() {
    AppTheme {
        FollowerUserCard(
            followerUser = FollowerUserDto(
                id = "1",
                username = "johndoe",
                displayName = "John Doe",
                avatar = "👤",
                verified = true,
                isFollowing = true
            )
        )
    }
}
