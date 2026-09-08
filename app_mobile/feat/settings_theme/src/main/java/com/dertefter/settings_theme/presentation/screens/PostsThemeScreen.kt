package com.dertefter.settings_theme.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor
import com.dertefter.design.R
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.components.post.AuthorUiModel
import com.dertefter.design.components.post.PostCard
import com.dertefter.design.components.post.PostUiModel
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.settings_theme.presentation.Event
import com.dertefter.settings_theme.presentation.UiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PostsThemeScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(text = stringResource(com.dertefter.settings_theme.R.string.settings_theme_posts_view))
                },
                navigationIcon = {
                    AppNavigationIcon(
                        icon = Icons.ArrowBack,
                        onClick = { onEvent(Event.OnNavigateBack) },
                        contentDescription = stringResource(R.string.design_back_content_desc)
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        }) { contentPadding ->

        val contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + MaterialTheme.spacing.medium,
            bottom = contentPadding.calculateBottomPadding() + MaterialTheme.spacing.medium,
            start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
            end = contentPadding.calculateEndPadding(LocalLayoutDirection.current)
        )

        LazyColumn(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
            contentPadding = contentPadding
        ) {

            item(key = "preview") {
                PostCard(
                    modifier = Modifier.animateItem(),
                    post = PostUiModel(
                        id = "1",
                        content = stringResource(com.dertefter.settings_theme.R.string.settings_theme_post_preview_text),
                        spans = emptyList(),
                        author = AuthorUiModel(
                            id = "", username = "username", displayName = "Name", avatar = "🦐", hasNuksta = false, verified = false, pin = null
                        ),
                        attachments = listOf(),
                        poll = null,
                        likesCount = 10,
                        isLiked = false,
                        commentsCount = 5,
                        repostsCount = 2,
                        isReposted = false,
                        viewsCount = 100,
                        dominantEmoji = "🍃",
                        isPinned = false,
                        isOwner = false,
                        createdAt = "2026-08-05T12:00:00Z",
                        editedAt = null,
                        originalPost = null
                    ), isOnMyWall = true, onHashtagClick = {},
                    onAttachmentClick = { _, _ -> },
                    onOpenPost = {},
                    onDelete = {},
                    onCommentsClick = {},
                    onPin = {},
                    onUnpin = {},
                    onVote = {},
                    onLike = {},
                    onUnlike = {},
                    onUserClick = {},
                    onRepostClick = {}
                )
            }

            item(key = "settings") {
                SegmentedColumn(
                    title = stringResource(com.dertefter.settings_theme.R.string.settings_theme_posts_appearance),
                    modifier = Modifier
                        .animateItem()
                        .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                ) {

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                        ){
                            Text(
                                stringResource(com.dertefter.settings_theme.R.string.settings_theme_post_containers),
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = uiState.postContained,
                                onCheckedChange = { onEvent(Event.OnUpdatePostContained(it)) }
                            )
                        }
                    }

                    if (uiState.postContained){
                        item {
                            Row(
                                modifier = Modifier.animateItem(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                            ){
                                Text(
                                    stringResource(com.dertefter.settings_theme.R.string.settings_theme_post_horizontal_padding),
                                    modifier = Modifier.weight(1f)
                                )
                                Switch(
                                    checked = uiState.postHorizontalExtraSpace,
                                    onCheckedChange = { onEvent(Event.OnUpdatePostHorizontalExtraSpace(it)) }
                                )
                            }
                        }
                    }


                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                        ){
                            Text(
                                stringResource(com.dertefter.settings_theme.R.string.settings_theme_post_show_username),
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = uiState.postShowUsername,
                                onCheckedChange = { onEvent(Event.OnUpdatePostShowUsername(it)) }
                            )
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                        ){
                            Text(
                                stringResource(com.dertefter.settings_theme.R.string.settings_theme_post_swap_date_username),
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = uiState.postSwapDateAndUsername,
                                onCheckedChange = { onEvent(Event.OnUpdatePostSwapDateAndUsername(it)) }
                            )
                        }
                    }
                }
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
private fun PostsThemeScreenPreview() {
    AppTheme {
        PostsThemeScreen(
            uiState = UiState(
                emojiAvatarHarmonizeColor = EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER,
                darkTheme = false,
                postHorizontalExtraSpace = true,
                postContained = true,
                postShowUsername = true,
                postSwapDateAndUsername = false
            ),
            onEvent = {}
        )
    }
}

