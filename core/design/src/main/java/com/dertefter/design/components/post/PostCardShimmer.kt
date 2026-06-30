package com.dertefter.design.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
fun PostCardShimmer(
    modifier: Modifier = Modifier
){

    Box(
        modifier = modifier
            .shimmer()
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
                ){
                    SmallEmojiAvatar(
                        emoji = "",
                        harmonizeColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy( MaterialTheme.spacing.small)
                    )
                    {
                        DisplayName(
                            name = "                                 ",
                            verified = false,
                            hasNuksta =false,
                            pin = null,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = "                   ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.weight(1f)
                )

            }

            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ){
                Text(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        .padding(horizontal = MaterialTheme.spacing.large),
                    text = "                                                                                    ",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                )
            }

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.largeIncreased)
                    .background(
                    MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    .width(280.dp)
                    .height(260.dp)
            )

            Row(
                modifier = Modifier
                    .padding(top = MaterialTheme.spacing.small)
                    .height(32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        .fillMaxHeight()
                        .padding(
                            horizontal = 24.dp
                        ),
                )

                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        .fillMaxHeight()
                        .padding(
                            horizontal = 24.dp
                        ),
                )

                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        .fillMaxHeight()
                        .padding(
                            horizontal = 24.dp
                        ),
                )
            }
        }
    }

}

@Composable
@Preview(showBackground = true)
fun PostCardShimmerPrev(){
    AppTheme {
        PostCardShimmer(
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun FeedLoadingShimmer(){
    Column(
        verticalArrangement = Arrangement.spacedBy(36.dp),
        modifier = Modifier
            .padding(
            )
    ) {
        PostCardShimmer()
        PostCardShimmer()
        PostCardShimmer()
    }
}