package com.dertefter.design.components.post

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.dertefter.design.R
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DominantEmoji(
    modifier: Modifier = Modifier,
    dominantEmoji: String
){

    val floatSpec = MaterialTheme.motionScheme.slowEffectsSpec<Float>()
    val intOffsetSpec = MaterialTheme.motionScheme.slowEffectsSpec<IntOffset>()

    val containerColor = Color.Transparent

    val contentColor = MaterialTheme.colorScheme.onBackground

    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Start
        ),
        tooltip = {
            PlainTooltip(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(R.string.design_post_captured_by_clan),
                    style = MaterialTheme.typography.labelLargeEmphasized,
                    modifier = Modifier.padding(MaterialTheme.spacing.small)
                )

            }
        },
        state = tooltipState
    ) {
        Row(
            modifier = modifier
                .clip(MaterialTheme.shapes.large)
                .background(containerColor)
                .clickable {
                    scope.launch { tooltipState.show() }
                },
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
}

@Preview(showBackground = true)
@Composable
fun DominantEmojiPreview() {
    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ){

        }
        DominantEmoji(
            dominantEmoji = "🙃"
        )
    }
}
