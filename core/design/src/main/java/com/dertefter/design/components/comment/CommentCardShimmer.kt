package com.dertefter.design.components.comment

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.valentinilk.shimmer.shimmer

@Composable
fun CommentCardShimmer(
    modifier: Modifier = Modifier
){

    Box(
        modifier = modifier
            .shimmer()
            .fillMaxWidth()
    ) {

        Box(
            modifier = modifier
                .padding(bottom = MaterialTheme.spacing.large)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        SmallEmojiAvatar(
                            emoji = "",
                            containerSize = 40.dp,
                            harmonizeColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column (
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                        ) {
                            DisplayName(
                                name = "                                 ",
                                verified = false,
                                hasNuksta =false,
                                pin = null,
                                textStyle = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = "                   ",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
                            )

                        }
                    }

                }

                Text(
                    text = "                                                                                             ",
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        ,
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                        .padding(top = MaterialTheme.spacing.small)
                        .height(28.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                            .fillMaxHeight()
                            .padding(
                                horizontal = 22.dp
                            ),
                    )
                }
            }
        }
    }

}

@Composable
fun CommentsLoadingShimmer(){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CommentCardShimmer()
        CommentCardShimmer()
        CommentCardShimmer()
    }
}

@Composable
@Preview(showBackground = true)
fun CommentCardShimmerPrev(){
    AppTheme {
        CommentsLoadingShimmer(
        )
    }
}
