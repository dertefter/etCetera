package com.dertefter.user.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import coil.compose.AsyncImage
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.components.post.AuthorUiModel
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.WearableTheme
import com.dertefter.design.theme.spacing

@Composable
fun Header(
    modifier: Modifier = Modifier,
    bannerUrl: String?,
    author: AuthorUiModel,
    isMe: Boolean = false,
    avatarSize: Dp = 40.dp,
    onBannerClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
){

   Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ){
            if (!bannerUrl.isNullOrEmpty()){
                AsyncImage(
                    model = bannerUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(bottom = avatarSize / 2)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .clickable(onClick = onBannerClick)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .fillMaxWidth()
                        .height(64.dp),
                    contentScale = ContentScale.Crop,
                )
            }


            SmallEmojiAvatar(
                emoji = author.avatar,
                containerSize = avatarSize,
                fontSize = 14.sp
            )
        }

       DisplayName(
           name = author.displayName,
           verified = author.verified,
           hasNuksta = author.hasNuksta,
           pin = author.pin,
           textStyle = MaterialTheme.typography.titleSmall
       )
       Text(
           text = "@${author.username}",
           style = MaterialTheme.typography.bodyExtraSmall,
           color = MaterialTheme.colorScheme.secondary,
           modifier = Modifier.padding(top = MaterialTheme.spacing.extraSmall)
       )

    }
}

@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    device = "id:wearos_small_round"
)
@Composable
fun HeaderPreview() {
    WearableTheme {
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
            isMe = true,
        )
    }
}
