package com.dertefter.design.components.post

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.dertefter.design.R
import com.dertefter.design.common.PrettifyInt
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.customColors
import com.dertefter.design.theme.spacing
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LikeButton(
    modifier: Modifier = Modifier,
    likes: Int,
    isLiked: Boolean,
    onClick: () -> Unit = {}
) {

    val floatSpec = MaterialTheme.motionScheme.slowEffectsSpec<Float>()
    val intOffsetSpec = MaterialTheme.motionScheme.slowEffectsSpec<IntOffset>()

    val likeButtonDescription = if (isLiked) {
        stringResource(R.string.design_like_button_unlike)
    } else {
        stringResource(R.string.design_like_button_like)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedHorizontalPadding by animateDpAsState(
        targetValue = if (isPressed) MaterialTheme.spacing.extraLarge else MaterialTheme.spacing.medium,
        label = "horizontalPadding",
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )

    val containerColor by animateColorAsState(
        if (isLiked) MaterialTheme.customColors.likeContainerColor else MaterialTheme.colorScheme.surfaceVariant,
        label = "containerColor"
    )

    val contentColor by animateColorAsState(
        if (isLiked) MaterialTheme.customColors.onLikeContainerColor else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "contentColor"
    )

    val scale = remember { Animatable(1f) }
    var isFirstRun by remember { mutableStateOf(true) }

    val prettifiedLikes = remember(likes) { likes.PrettifyInt() }
    val animatedLikes = remember(prettifiedLikes) { likes }

    LaunchedEffect(isLiked) {
        if (isLiked && !isFirstRun) {
            scale.animateTo(1.15f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        } else {
            scale.animateTo(1f, floatSpec)
        }
        isFirstRun = false
    }

    Row(
        modifier = modifier
            .shadow(
                elevation = 20.dp,
                shape = CircleShape,
                ambientColor = MaterialTheme.customColors.likeContainerColor,
                spotColor = Color.Transparent
            )
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick,
            )
            .semantics {
                contentDescription = likeButtonDescription
            }
            .background(containerColor)
            .animateContentSize(
                animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
            )
            .padding(
                horizontal = animatedHorizontalPadding,
                vertical = MaterialTheme.spacing.small + MaterialTheme.spacing.extraSmall,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        AnimatedContent(
            targetState = isLiked,
            transitionSpec = {
                (scaleIn(floatSpec) + fadeIn(floatSpec)).togetherWith(
                    scaleOut(floatSpec) + fadeOut(floatSpec)
                )
            },
            label = "icon"
        ) { liked ->
            Icon(
                imageVector = if (liked) Icons.FavFilled else Icons.Fav,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .scale(scale.value),
                tint = contentColor
            )
        }

        if (likes > 0) {
            AnimatedContent(
                targetState = animatedLikes,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically(intOffsetSpec) { it } + fadeIn(floatSpec) togetherWith
                                slideOutVertically(intOffsetSpec) { -it } + fadeOut(floatSpec)
                    } else {
                        slideInVertically(intOffsetSpec) { -it } + fadeIn(floatSpec) togetherWith
                                slideOutVertically(intOffsetSpec) { it } + fadeOut(floatSpec)
                    }.using(SizeTransform(clip = false))
                },
                label = "likes"
            ) { count ->
                val fontWeight = if (isLiked) FontWeight.Bold else FontWeight.Medium
                Text(
                    text = count.PrettifyInt(),
                    color = contentColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = fontWeight)
                )
            }
        }

    }
}

@Preview(showBackground = true, widthDp = 100, heightDp = 100)
@Composable
fun LikeButtonPreview() {
    AppTheme() {
        val values = listOf(1, 2, 6, 10, 100, 200, 1000, 10000, 0)
        var index by remember { mutableIntStateOf(0) }
        var isLiked by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(8000.milliseconds)
                index = (index + 1) % values.size
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            LikeButton(
                likes = values[index],
                isLiked = isLiked,
                onClick = { isLiked = !isLiked }
            )
        }

    }
}
