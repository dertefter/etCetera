package com.dertefter.user.presentation.component

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.dertefter.design.common.PrettifyInt
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@Composable
fun WideTitleValueCard(
    modifier: Modifier = Modifier,
    title: String,
    value: Int,
    onClick: () -> Unit = {}
){
    val floatSpec = MaterialTheme.motionScheme.slowEffectsSpec<Float>()
    val intOffsetSpec = MaterialTheme.motionScheme.slowEffectsSpec<IntOffset>()

    val prettifiedValue = remember(value) { value.PrettifyInt() }
    val animatedValue = remember(prettifiedValue) { value }

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(onClick = onClick)
            .padding(
                horizontal = MaterialTheme.spacing.large,
                vertical = MaterialTheme.spacing.medium
            ),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(
            targetState = animatedValue,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically(intOffsetSpec) { it } + fadeIn(floatSpec) togetherWith
                            slideOutVertically(intOffsetSpec) { -it } + fadeOut(floatSpec)
                } else {
                    slideInVertically(intOffsetSpec) { -it } + fadeIn(floatSpec) togetherWith
                            slideOutVertically(intOffsetSpec) { it } + fadeOut(floatSpec)
                }.using(SizeTransform(clip = false))
            },
            label = "value"
        ) { count ->
            Text(
                text = count.PrettifyInt(),
                style = MaterialTheme.typography.bodyLargeEmphasized,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,

        )

    }
}

@Preview(showBackground = true)
@Composable
fun WidejjjjJJVJ(){
    AppTheme() {
        WideTitleValueCard(
            title = "Подписки",
            value = 13844
        )
    }
}