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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.components.post.AuthorUiModel
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import kotlin.math.absoluteValue
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Header(
    modifier: Modifier = Modifier,
    bannerUrl: String?,
    author: AuthorUiModel,
    avatarSize: Dp = 120.dp,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onBannerClick: () -> Unit = {}
){


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
                    .height(172.dp),
                contentScale = ContentScale.Crop,
            )

            SmallEmojiAvatar(
                emoji = author.avatar,
                containerSize = avatarSize,
                fontSize = 32.sp,
                strokeWidth = 4.dp + (scrollFraction * 10).dp
            )
        }

       DisplayName(
           name = author.displayName,
           verified = author.verified,
           hasNuksta = author.hasNuksta,
           pin = author.pin,
           textStyle = MaterialTheme.typography.displaySmall
       )
       Text(
           text = "@${author.username}",
           style = MaterialTheme.typography.labelLargeEmphasized,
           color = MaterialTheme.colorScheme.secondary,
           modifier = Modifier.padding(top = MaterialTheme.spacing.extraSmall)
       )

    }
}