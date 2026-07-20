package com.dertefter.feed.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dertefter.data.common.Constants
import com.dertefter.data.dto.search.SearchHashtagDto
import com.dertefter.design.common.PrettifyInt
import com.dertefter.design.components.avatar.EmojiAvatar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.feed.R
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun FeedAppBar(
    modifier: Modifier = Modifier,
    profileEmoji: String?,
    notificationCount: Int = 0,
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    popularHashtags: List<SearchHashtagDto>? = emptyList(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    ){

    val searchHint = stringResource(id = R.string.feed_search_hint)
    val hashtagFormat = stringResource(id = R.string.hashtag_search_item_format)
    var currentSearchText by remember(searchHint) { mutableStateOf(searchHint) }

    LaunchedEffect(popularHashtags, searchHint, hashtagFormat) {
        if (!popularHashtags.isNullOrEmpty()) {
            val items = listOf(searchHint) + popularHashtags.map {
                hashtagFormat.format(it.name, it.postsCount.PrettifyInt())
            }
            while (true) {
                delay(Constants.STATS_UPDATE_DELAY_MS.milliseconds)
                currentSearchText = items.random()
            }
        } else {
            currentSearchText = searchHint
        }
    }
    TopAppBar(
        modifier = modifier
            .padding(vertical = MaterialTheme.spacing.medium),
        scrollBehavior = scrollBehavior,
        contentPadding = PaddingValues(horizontal = 4.dp),
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        navigationIcon = {
            NotificationIconWithBadge(
                notificationCount = notificationCount,
                onClick = onNotificationsClick,
            )
        },
        actions = {
            EmojiAvatar(
                emoji = profileEmoji ?: "",
                containerSize = 56.dp,
                fontSize = 24.sp,
                onClick = onProfileClick,
                modifier = Modifier.padding(start = 2.dp)
            )
        },
        title = {
            Box(
                modifier = Modifier
                    .height(52.dp)
                    .clip(CircleShape)
                    .clickable(
                        onClick = onSearchClick
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            ){
                AnimatedContent(
                    targetState = currentSearchText,
                    transitionSpec = {
                        (slideInVertically { height -> height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> -height } + fadeOut()
                        )
                    },
                    label = "SearchTextAnimation",
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.spacing.large)
                        .align(Alignment.Center)
                        .fillMaxWidth()
                ) { text ->
                    Text(
                        text,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

        }
    )
}

@Composable
private fun NotificationIconWithBadge(
    notificationCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        AppNavigationIcon(
            icon = Icons.Notifications,
            contentDescription = stringResource(id = R.string.notifications_content_description),
            onClick = onClick,
            modifier = Modifier.size(60.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
        AnimatedVisibility(
            visible = notificationCount > 0,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}

@Composable
@Preview
fun FeedAppBarPREVIEW(){
    AppTheme() {
        FeedAppBar(
            profileEmoji = "",
            notificationCount = 12,
        )
    }
}