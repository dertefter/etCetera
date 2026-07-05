package com.dertefter.design.components.post

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.dertefter.design.R
import com.dertefter.design.common.PrettifyInt
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@Composable
fun RepostButton(
    modifier: Modifier = Modifier,
    reposts: Int,
    isReposted: Boolean,
    onClick: () -> Unit = {},
){

    val floatSpec = MaterialTheme.motionScheme.slowEffectsSpec<Float>()
    val intOffsetSpec = MaterialTheme.motionScheme.slowEffectsSpec<IntOffset>()

    val containerColor by animateColorAsState(
        if (isReposted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
        label = "containerColor"
    )

    val contentColor by animateColorAsState(
        if (isReposted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "contentColor"
    )

    val desc = if (isReposted) {
        stringResource(R.string.design_unrepost)
    } else {
        stringResource(R.string.design_repost)
    }


    val scale = remember { Animatable(1f) }
    var isFirstRun by remember { mutableStateOf(true) }

    LaunchedEffect(isReposted) {
        if (isReposted && !isFirstRun) {
            scale.animateTo(1.15f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        } else {
            scale.animateTo(1f, floatSpec)
        }
        isFirstRun = false
    }

    val icon = Icons.Cached

    val prettifiedReposts = remember(reposts) { reposts.PrettifyInt() }
    val animatedReposts = remember(prettifiedReposts) { reposts }

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = desc
            }
            .background(containerColor)
            .padding(
                all = MaterialTheme.spacing.medium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ){
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .scale(scale.value),
            tint = contentColor
        )
        if (reposts > 0){
            AnimatedContent(
                targetState = animatedReposts,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically(intOffsetSpec) { it } + fadeIn(floatSpec) togetherWith
                                slideOutVertically(intOffsetSpec) { -it } + fadeOut(floatSpec)
                    } else {
                        slideInVertically(intOffsetSpec) { -it } + fadeIn(floatSpec) togetherWith
                                slideOutVertically(intOffsetSpec) { it } + fadeOut(floatSpec)
                    }.using(SizeTransform(clip = false))
                },
                label = "reposts"
            ) { count ->
                Text(
                    text = count.PrettifyInt(),
                    color = contentColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

    }
}

@Preview(showBackground = false)
@Composable
fun RepostButtonPreview() {
    AppTheme {
        RepostButton(
            reposts = 10,
            isReposted = true
        )
    }
}
@Preview(showBackground = false)
@Composable
fun RepostButtonPreview2() {
    AppTheme {
        RepostButton(
            reposts = 0,
            isReposted = false
        )
    }
}
