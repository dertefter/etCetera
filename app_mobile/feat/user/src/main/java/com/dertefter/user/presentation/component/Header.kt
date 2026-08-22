package com.dertefter.user.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dertefter.data.dto.user.LastSeenDto
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.avatar.EmojiAvatar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.post.AuthorUiModel
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.user.R
import com.dertefter.user.presentation.mapper.toPresentationString
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlin.math.absoluteValue
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Header(
    modifier: Modifier = Modifier,
    bannerUrl: String?,
    author: AuthorUiModel,
    isMe: Boolean = false,
    avatarSize: Dp = 120.dp,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onBannerClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    followersCount: Int? = null,
    followingCount: Int? = null,
    onFollowersClick: () -> Unit,
    onFollowingClock: () -> Unit,
    lastSeenDto: LastSeenDto? = null,
    isOnline: Boolean = false,
){

    val context = LocalContext.current

    val hazeState = rememberHazeState()

    val scrollFraction = if (scrollBehavior != null){
        (scrollBehavior.state.contentOffset.absoluteValue / (scrollBehavior.state.heightOffsetLimit.absoluteValue * 4)).coerceIn(0f, 1f)
    }else{
        0f
    }

    val rotation = scrollFraction * 90f

   Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ){
            if (!bannerUrl.isNullOrEmpty() || isMe){
                Box(
                    modifier = Modifier
                        .blur(120.dp * scrollFraction, edgeTreatment =  BlurredEdgeTreatment.Unbounded)
                        .padding(bottom = avatarSize / 2)
                        .alpha(sqrt(1f - scrollFraction))
                        .clip(MaterialTheme.shapes.extraLarge)
                        .clickable(onClick = onBannerClick)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .fillMaxWidth()
                        .height(172.dp),
                ){
                    AsyncImage(
                        model = bannerUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .hazeSource(state = hazeState)
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )

                    if (isMe){
                        AppNavigationIcon(
                            onClick = onEditClick,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(MaterialTheme.spacing.medium),
                            containerColor = MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            icon = Icons.Edit,
                            contentDescription = stringResource(R.string.user_edit_banner),
                            hazeState = hazeState
                        )
                    }
                }
            }
            



            EmojiAvatar(
                emoji = author.avatar,
                rotation = rotation,
                containerSize = avatarSize,
                fontSize = 36.sp,
                isOnline = isOnline
            )
        }

       DisplayName(
           name = author.displayName,
           verified = author.verified,
           hasNuksta = author.hasNuksta,
           pin = author.pin,
           textStyle = MaterialTheme.typography.titleLarge
       )
       Text(
           text = "@${author.username}",
           style = MaterialTheme.typography.bodyLargeEmphasized,
           color = MaterialTheme.colorScheme.secondary,
           modifier = Modifier.padding(top = MaterialTheme.spacing.extraSmall)
       )

       val lastSeenText = if (!isMe && !isOnline) {
           lastSeenDto.toPresentationString(context, false)
       } else if (!isMe) {
           stringResource(R.string.user_online)
       } else {
           null
       }

       val color by animateColorAsState(
           if (isOnline){
               MaterialTheme.colorScheme.tertiary
           }else {
               MaterialTheme.colorScheme.outline
           }
       )

       val fontWeight by animateIntAsState(
           if (isOnline){
               600
           }else {
               400
           }
       )

       if (lastSeenText != null) {
           Text(
               text = lastSeenText,
               style = MaterialTheme.typography.bodyMedium,
               color = color,
               fontWeight = FontWeight(fontWeight),
               modifier = Modifier.padding(vertical = MaterialTheme.spacing.medium)
           )
       }

       Row(
           modifier = Modifier
               .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
           horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.defaultScreenPadding)
       ) {
           followersCount?.let { followersCount ->
               TitleValueCard(
                   title = stringResource(R.string.user_followers),
                   value = followersCount,
                   onClick = onFollowersClick
               )
           }
           
           followingCount?.let { followingCount ->
               TitleValueCard(
                   title = stringResource(R.string.user_following),
                   value = followingCount,
                   onClick = onFollowingClock
               )
           }


           

       }

    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0x00121318
)
@Composable
fun HeaderPreview() {
    AppTheme {
        Header(
            bannerUrl = "https://picsum.photos/800/200",
            author = AuthorUiModel(
                id = "author1",
                username = "johndoe",
                displayName = "John Doe",
                avatar = "😐",
                hasNuksta = true,
                verified = true,
                pin = null,
            ),
            isMe = false,
            isOnline = true,
            followersCount = 1,
            onFollowersClick = {},
            onFollowingClock = {},
        )
    }
}
