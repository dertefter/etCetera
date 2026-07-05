package com.dertefter.user.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.OutlinedButton
import androidx.wear.compose.material3.Text
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.user.UserDto
import com.dertefter.data.dto.user.VisibilityDto
import com.dertefter.design.components.common.TransformingListItem
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.theme.WearableTheme
import com.dertefter.design.theme.spacing
import com.dertefter.user.R
import com.dertefter.user.presentation.component.BioCard
import com.dertefter.user.presentation.component.Header
import com.dertefter.user.presentation.component.TitleValueCard
import com.dertefter.user.presentation.mapper.toUiModel
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult

@Composable
fun UserScreen(
    onEvent: (Event) -> Unit,
    userUiState: UserUiState,
    uiState: PaginatorUiState<PostDto>,
    paginator: MutableCursorPaginator<String, PostDto>,
) {

    val listState = rememberTransformingLazyColumnState()

    if (userUiState.userDto != null) {
        Feed(
            paginator = paginator,
            onEvent = onEvent,
            uiState = uiState,
            listState = listState,
            isMe = userUiState.isMe,
            header = { transformationSpec ->

                item(key = "user_header") {
                    TransformingListItem(transformationSpec = transformationSpec) {
                        Header(
                            bannerUrl = userUiState.userDto.banner,
                            author = userUiState.userDto.toUiModel(),
                            isMe = userUiState.isMe,
                            onEditClick = {
                                if (userUiState.isMe) {
                                    onEvent(Event.OnBannerEdit)
                                }
                            }
                        )
                    }
                }

                item(key = "user_stats") {
                    TransformingListItem(transformationSpec = transformationSpec) {
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                        ) {
                            TitleValueCard(
                                modifier = Modifier.weight(1f),
                                title = stringResource(R.string.user_followers),
                                value = userUiState.userDto.followersCount,
                                onClick = {
                                    onEvent(Event.OnOpenFollowers(userId = userUiState.userDto.id))
                                }
                            )

                            TitleValueCard(
                                modifier = Modifier.weight(1f),
                                title = stringResource(R.string.user_following),
                                value = userUiState.userDto.followingCount,
                                onClick = {
                                    onEvent(Event.OnOpenFollowing(userId = userUiState.userDto.id))
                                }
                            )
                        }
                    }
                }

                if (!userUiState.userDto.bio.isNullOrEmpty()) {
                    item(key = "user_bio") {
                        TransformingListItem(transformationSpec = transformationSpec) {
                            BioCard(
                                bio = userUiState.userDto.bio ?: "",
                            )
                        }
                    }
                }

                if (!userUiState.isMe) {
                    item(key = "user_follow_button") {
                        TransformingListItem(transformationSpec = transformationSpec) {
                            if (userUiState.userDto.isFollowing) {
                                Button(
                                    onClick = {
                                        onEvent(Event.OnUnfollow(userUiState.userDto.id))
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        stringResource(R.string.user_unfollow),
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        onEvent(Event.OnFollow(userUiState.userDto.id))
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                ) {
                                    Text(
                                        stringResource(R.string.user_follow),
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (userUiState.isLoading) {
                AppLoadingIndicator()
            }
        }
    }
}

@Preview(device = "id:wearos_large_round")
@Composable
fun UserScreenPreview() {
    WearableTheme {
        val samplePaginator = mutableCursorPaginator {
            load {
                CursorLoadResult(
                    data = emptyList<PostDto>(),
                    bookmark = CursorBookmark(null, "initial", null)
                )
            }
        }
        UserScreen(
            onEvent = {},
            userUiState = UserUiState(
                userDto = UserDto(
                    avatar = "🥳",
                    banner = "https://example.com/banner.png",
                    bio = "Developer & Designer",
                    createdAt = "2023-01-01T00:00:00Z",
                    displayName = "Dertefter Labs",
                    followersCount = 120,
                    followingCount = 80,
                    id = "1",
                    hasNuksta = false,
                    isFollowedBy = false,
                    isFollowing = false,
                    lastSeen = null,
                    likesVisibility = VisibilityDto.EVERYONE,
                    online = true,
                    pin = null,
                    pinnedPostId = null,
                    postsCount = 42,
                    username = "dertefter",
                    verified = true,
                    wallAccess = VisibilityDto.EVERYONE
                ),
                isLoading = false
            ),
            uiState = PaginatorUiState.Idle,
            paginator = samplePaginator
        )
    }
}
