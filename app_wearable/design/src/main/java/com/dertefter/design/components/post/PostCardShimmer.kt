package com.dertefter.design.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.theme.WearableTheme
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
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                ){
                    SmallEmojiAvatar(
                        emoji = "",
                        modifier = Modifier.size(32.dp),
                        harmonizeColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column(
                    )
                    {
                        DisplayName(
                            name = "                ",
                            verified = false,
                            hasNuksta =false,
                            pin = null,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = "          ",
                            style = MaterialTheme.typography.bodyExtraSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.weight(1f)
                )

            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant),
                text = " ",
                style = MaterialTheme.typography.bodySmall,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(
                        MaterialTheme.colorScheme.onSurfaceVariant
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
            ) {
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(48.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(48.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(48.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
    }

}

@Composable
@Preview(showBackground = true, device = "id:wearos_small_round")
fun PostCardShimmerPrev(){
    WearableTheme {
        PostCardShimmer()
    }
}