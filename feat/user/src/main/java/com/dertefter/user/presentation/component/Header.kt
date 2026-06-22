package com.dertefter.user.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.post.AuthorUiModel
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
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
    onEditClick: () -> Unit = {}
){

    val hazeState = rememberHazeState()

    val scrollFraction = if (scrollBehavior != null){
        (scrollBehavior.state.contentOffset.absoluteValue / (scrollBehavior.state.heightOffsetLimit.absoluteValue * 4)).coerceIn(0f, 1f)
    }else{
        0f
    }
    val avatarSize = avatarSize * (1 - scrollFraction)

   Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ){
            if (!bannerUrl.isNullOrEmpty() || isMe){
                AsyncImage(
                    model = bannerUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(bottom = avatarSize / 2)
                        .alpha(sqrt(1f - scrollFraction))
                        .clip(MaterialTheme.shapes.extraLarge)
                        .clickable(onClick = onBannerClick)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .fillMaxWidth()
                        .height(172.dp)
                        .hazeSource(state = hazeState),
                    contentScale = ContentScale.Crop,
                )
            }
            
            if (isMe){
                AppNavigationIcon(
                    onClick = onEditClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(MaterialTheme.spacing.medium),
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    icon = Icons.Edit,
                    contentDescription = "Изменить баннер",
                    hazeState = null
                )
            }


            SmallEmojiAvatar(
                emoji = author.avatar,
                containerSize = avatarSize,
                fontSize = 32.sp,
                strokeWidth = 6.dp + (scrollFraction * 10).dp
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

    }
}

@Preview(showBackground = true)
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
                pin = null
            )
        )
    }
}
