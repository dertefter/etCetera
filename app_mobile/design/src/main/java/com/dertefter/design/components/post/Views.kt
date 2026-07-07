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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.dertefter.design.common.PrettifyInt
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@Composable
fun Views(
    modifier: Modifier = Modifier,
    views: Int
){

    val floatSpec = MaterialTheme.motionScheme.slowEffectsSpec<Float>()
    val intOffsetSpec = MaterialTheme.motionScheme.slowEffectsSpec<IntOffset>()

    val containerColor = Color.Transparent

    val contentColor = MaterialTheme.colorScheme.onBackground

    val icon = Icons.Visibility

    val prettifiedViews = remember(views) { views.PrettifyInt() }
    val animatedViews = remember(prettifiedViews) { views }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .padding(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.small + MaterialTheme.spacing.extraSmall,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ){
        Icon(
            imageVector = icon,
            contentDescription = null,
            Modifier.size(20.dp),
            tint = contentColor
        )
        if (views > 0){
            AnimatedContent(
                targetState = animatedViews,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically(intOffsetSpec) { it } + fadeIn(floatSpec) togetherWith
                                slideOutVertically(intOffsetSpec) { -it } + fadeOut(floatSpec)
                    } else {
                        slideInVertically(intOffsetSpec) { -it } + fadeIn(floatSpec) togetherWith
                                slideOutVertically(intOffsetSpec) { it } + fadeOut(floatSpec)
                    }.using(SizeTransform(clip = false))
                },
                label = "views"
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

@Preview(showBackground = true)
@Composable
fun ViewsPreview() {
    AppTheme {
        Views(
            views = 10
        )
    }
}
@Preview(showBackground = true)
@Composable
fun ViewsPreview2() {
    AppTheme {
        Views(
            views = 0
        )
    }
}
