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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun CommentsButton(
    modifier: Modifier = Modifier,
    comments: Int,
    onClick: () -> Unit = {}
){

    val floatSpec = MaterialTheme.motionScheme.slowEffectsSpec<Float>()
    val intOffsetSpec = MaterialTheme.motionScheme.slowEffectsSpec<IntOffset>()

    val containerColor = MaterialTheme.colorScheme.surfaceContainer

    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant

    val desc = stringResource(R.string.design_comments)

    val prettifiedComments = remember(comments) { comments.PrettifyInt() }
    val animatedComments = remember(prettifiedComments) { comments }

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
            imageVector = Icons.Comment,
            contentDescription = null,
            Modifier.size(20.dp),
            tint = contentColor
        )
        if (comments > 0){
            AnimatedContent(
                targetState = animatedComments,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically(intOffsetSpec) { it } + fadeIn(floatSpec) togetherWith
                                slideOutVertically(intOffsetSpec) { -it } + fadeOut(floatSpec)
                    } else {
                        slideInVertically(intOffsetSpec) { -it } + fadeIn(floatSpec) togetherWith
                                slideOutVertically(intOffsetSpec) { it } + fadeOut(floatSpec)
                    }.using(SizeTransform(clip = false))
                },
                label = "comments"
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
fun CommentsButtonPreview() {
    AppTheme {
        CommentsButton(
            comments = 10
        )
    }
}
@Preview(showBackground = false)
@Composable
fun CommentsButtonPreview2() {
    AppTheme {
        CommentsButton(
            comments = 0
        )
    }
}
