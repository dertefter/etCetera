package com.dertefter.design.components.post

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.R
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.avatar.EmojiAvatar
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun OriginalPostCard(
    originalPost: OriginalPostUiModel,
    modifier: Modifier = Modifier,
    onOpenPost: (String) -> Unit = {},
    onHashtagClick: (String) -> Unit = {},
    onUserClick: (String) -> Unit = {},
    onLinkClick: ((String) -> Unit)? = null,
    onAttachmentClick: (attachments: List<AttachmentUiModel>, position: Int) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val finalOnLinkClick = onLinkClick ?: { url -> uriHandler.openUri(url) }
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.largeIncreased)
            .clickable(onClick = {onOpenPost(originalPost.id)})
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.largeIncreased
            )
            .padding(all = MaterialTheme.spacing.large),
    ) {
        if (originalPost.isDeleted) {
            Text(
                text = stringResource(R.string.design_post_deleted),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    EmojiAvatar(
                        emoji = originalPost.author.avatar,
                        onClick = { onUserClick(originalPost.author.id) },
                        containerSize = 48.dp
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable(
                                onClick = { onUserClick(originalPost.author.id) })
                    ) {
                        DisplayName(
                            name = originalPost.author.displayName,
                            verified = originalPost.author.verified,
                            hasNuksta = originalPost.author.hasNuksta,
                            pin = originalPost.author.pin,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "@${originalPost.author.username}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    PrettyDate(
                        createdDate = originalPost.getCreatedAtDate(),
                        editedDate = originalPost.getEditedAtDate(),
                        modifier = Modifier
                    )

                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(
                            onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.MoreHoriz, contentDescription = ""
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            shape = MaterialTheme.shapes.largeIncreased,
                            onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.design_post_copy_link)) },
                                onClick = {
                                    val link =
                                        "https://итд.com/@${originalPost.author.username}/post/${originalPost.id}"
                                    scope.launch {
                                        clipboard.setClipEntry(
                                            ClipEntry(
                                                ClipData.newPlainText(
                                                    null, link
                                                )
                                            )
                                        )
                                    }
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.ContentCopy, contentDescription = null)
                                })
                        }
                    }
                }
                if (originalPost.content.isNotEmpty()) {
                    var revealedSpoilers by remember { mutableStateOf(setOf<Int>()) }
                    val annotatedString = buildPostAnnotatedString(originalPost.content, originalPost.spans, revealedSpoilers)
                    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
                    Text(
                        modifier = Modifier
                            .pointerInput(originalPost.id, revealedSpoilers) {
                                detectTapGestures { offset ->
                                    layoutResult?.let { lr ->
                                        val tapOffset = lr.getOffsetForPosition(offset)
                                        val hashtagAnnotations = annotatedString.getStringAnnotations(
                                            tag = "HASHTAG",
                                            start = tapOffset,
                                            end = tapOffset
                                        )
                                        val mentionAnnotations = annotatedString.getStringAnnotations(
                                            tag = "MENTION",
                                            start = tapOffset,
                                            end = tapOffset
                                        )
                                        val spoilerAnnotations = annotatedString.getStringAnnotations(
                                            tag = "SPOILER",
                                            start = tapOffset,
                                            end = tapOffset
                                        )
                                        val linkAnnotations = annotatedString.getStringAnnotations(
                                            tag = "LINK",
                                            start = tapOffset,
                                            end = tapOffset
                                        )
                                        if (hashtagAnnotations.isNotEmpty()) {
                                            onHashtagClick(hashtagAnnotations.first().item)
                                        } else if (mentionAnnotations.isNotEmpty()) {
                                            onUserClick(mentionAnnotations.first().item)
                                        } else if (linkAnnotations.isNotEmpty()) {
                                            finalOnLinkClick(linkAnnotations.first().item)
                                        } else if (spoilerAnnotations.isNotEmpty()) {
                                            spoilerAnnotations.firstOrNull()?.let { annotation ->
                                                revealedSpoilers = revealedSpoilers + annotation.item.toInt()
                                            }
                                        } else {
                                            onOpenPost(originalPost.id)
                                        }
                                    }
                                }
                            },
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { layoutResult = it }
                    )
                }
                if (originalPost.attachments.isNotEmpty()) {
                    AttachmentsCarousel(
                        attachments = originalPost.attachments,
                        modifier = Modifier.fillMaxWidth(),
                        itemShape = MaterialTheme.shapes.medium,
                        itemHeight = 180.dp,
                        onItemClick = { position ->
                            onAttachmentClick(
                                originalPost.attachments, position
                            )
                        }
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
                    spans = emptyList(),
                    author = AuthorUiModel(
                        id = "user1",
                        username = "johndoe",
                        displayName = "John Doe",
                        avatar = "😊",
                        hasNuksta = true, verified = true, pin = null
                    ),
                    attachments = listOf(
                        AttachmentUiModel(
                            id = "a1",
                            type = "image",
                            url = "https://picsum.photos/400/300"
                        )
                    ),
                    poll = null,
                    createdAt = "2024-08-05T12:00:00Z",
                    editedAt = null,
                    isDeleted = false
                ),
                onAttachmentClick = {_,_ -> }
            )
        }
    }
}
