package com.dertefter.search.presentation.component

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.search.SearchUserDto
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@Composable
fun SearchUserCard(
    searchUser: SearchUserDto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = MaterialTheme.spacing.large, vertical = MaterialTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
    ) {
        SmallEmojiAvatar(
            emoji = searchUser.avatar,
            containerSize = 40.dp
        )
        Column {
            DisplayName(
                name = searchUser.displayName,
                verified = searchUser.verified,
                pin = null,
                hasNuksta = searchUser.hasNuksta,
            )
            Text(
                text = "@${searchUser.username}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchUserCardPreview() {
    AppTheme {
        SearchUserCard(
            searchUser = SearchUserDto(
                id = "1",
                username = "johndoe",
                displayName = "John Doe",
                avatar = "👤",
                verified = true,
                hasNuksta = false,
                followersCount = 12
            )
        )
    }
}

