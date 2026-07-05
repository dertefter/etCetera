package com.dertefter.design.components.poll

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollOption(
    modifier: Modifier = Modifier,
    text: String,
    percent: Float?,
    isChecked: Boolean = true,
    onClick: () -> Unit = {}
){

    val bgColor by animateColorAsState(
        targetValue = if (isChecked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    )

    val contentColor by animateColorAsState(
        targetValue = if (isChecked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    )

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(bgColor)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        if (percent != null) {
            Box(
                modifier = Modifier.matchParentSize()
            ) {
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .fillMaxWidth(percent)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(MaterialTheme.spacing.large)
        ) {
            AnimatedVisibility(
                visible = isChecked
            ) {
                Icon(
                    imageVector = Icons.Check,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = MaterialTheme.spacing.medium)
                )
            }

            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )

            if (percent != null) {
                Text(
                    text = "${(percent * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PollOptionPreview2() {
    AppTheme {
        PollOption(
            text = "11111",
            percent = 0.6f,
            isChecked = true
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PollOptionPreview() {
    AppTheme {
        PollOption(
            text = "11111",
            isChecked = false,
            percent = 0.6f
        )
    }
}
