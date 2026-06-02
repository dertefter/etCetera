package com.dertefter.design.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.components.poll.PollCard
import com.dertefter.design.theme.spacing

@Composable
fun OriginalPostCard(
    originalPost: OriginalPostUiModel,
    modifier: Modifier = Modifier,
    onOpenPost: (String) -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.largeIncreased)
            .clickable(onClick = {onOpenPost(originalPost.id)})
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.extraLarge
            )
            .padding(vertical = MaterialTheme.spacing.large),
    ) {
        if (originalPost.isDeleted) {
            Text(
                text = "Пост был удален",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.large)
                    .align(Alignment.Center)
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Row(
                    modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.large),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    SmallEmojiAvatar(
                        emoji = originalPost.author.avatar,
                        containerSize = 32.dp
                    )
                    Column {
                        Text(
                            text = originalPost.author.displayName,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "@${originalPost.author.username}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (originalPost.content.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.spacing.large),
                        text = originalPost.content,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (originalPost.attachments.isNotEmpty()) {
                    AttachmentsCarousel(
                        attachments = originalPost.attachments,
                        modifier = Modifier.fillMaxWidth(),
                        itemHeight = 160.dp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OriginalPostCardPreview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OriginalPostCard(
                originalPost = OriginalPostUiModel(
                    id = "1",
                    content = "This is a sample post content with some text to see how it looks in the card.",
                    author = AuthorUiModel(
                        id = "user1",
                        username = "johndoe",
                        displayName = "John Doe",
                        avatar = "😊"
                    ),
                    attachments = listOf(
                        AttachmentUiModel(
                            id = "a1",
                            type = "image",
                            url = "https://picsum.photos/400/300"
                        )
                    ),
                    isDeleted = false,
                    poll = null
                )
            )
        }
    }
}
