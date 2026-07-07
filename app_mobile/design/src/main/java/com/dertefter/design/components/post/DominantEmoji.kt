package com.dertefter.design.components.post

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@Composable
fun DominantEmoji(
    modifier: Modifier = Modifier,
    dominantEmoji: String
){

    val floatSpec = MaterialTheme.motionScheme.slowEffectsSpec<Float>()
    val intOffsetSpec = MaterialTheme.motionScheme.slowEffectsSpec<IntOffset>()

    val containerColor = Color.Transparent

    val contentColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(containerColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ){

        AnimatedContent(
            targetState = dominantEmoji,
            transitionSpec = {
                slideInVertically(intOffsetSpec) { it } + fadeIn(floatSpec) togetherWith
                        slideOutVertically(intOffsetSpec) { -it } + fadeOut(floatSpec) using
                        SizeTransform(clip = false)
            },
            label = "dominantEmoji"
        ) { emoji ->
            Text(
                text = emoji,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DominantEmojiPreview() {
    AppTheme {
        DominantEmoji(
            dominantEmoji = "🙃"
        )
    }
}
