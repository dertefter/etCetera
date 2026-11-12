package com.dertefter.edbi_users.presentation.component

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
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.ebdi.EbdiUser
import com.dertefter.design.components.avatar.Avatar
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.lists.SegmentedContentItem
import com.dertefter.design.theme.spacing
import com.dertefter.design.transition.sharedElementByKey

@Composable
fun EbdiUserCard(
    user: EbdiUser,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    index: Int = 0,
    count: Int = 0
) {
    val sharedAvatarId = "avatar_${user.id}"
    val sharedNameId = "display_name_${user.id}"
    val sharedUsernameId = "username_${user.id}"

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
        ) {
            Avatar(
                data = user.avatar,
                containerSize = 48.dp,
                onClick = onClick,
                modifier = Modifier.sharedElementByKey(sharedAvatarId)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                DisplayName(
                    name = user.displayName.ifBlank { user.username },
                    verified = false,
                    hasNuksta = false,
                    pin = null,
                    modifier = Modifier.sharedElementByKey(sharedNameId)
                )
                Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.sharedElementByKey(sharedUsernameId)
                )
            }
        }
    }
}

@Composable
fun EbdiUserCardVertical(
    user: EbdiUser,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val sharedAvatarId = "avatar_${user.id}"
    val sharedNameId = "display_name_${user.id}"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        Avatar(
            data = user.avatar,
            containerSize = 86.dp,
            onClick = onClick,
            modifier = Modifier.sharedElementByKey(sharedAvatarId)
        )

        DisplayName(
            name = user.displayName.ifBlank { user.username },
            verified = false,
            hasNuksta = false,
            pin = null,
            modifier = Modifier
                .sharedElementByKey(sharedNameId)
                .padding(horizontal = MaterialTheme.spacing.large)
        )
    }
}
