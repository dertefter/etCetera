package com.dertefter.banner_edit.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.spacing

@Composable
fun ColorItem(
    modifier: Modifier = Modifier,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit = {}

) {

    val borderWidth by animateDpAsState(
        if (isSelected) 4.dp else 0.dp
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                onClick = onClick
            )
            .border(
                shape = CircleShape,
                width = borderWidth,
                color = color
            )
            .padding(borderWidth * 1.8f)
            .clip(CircleShape)
            .background(color = color),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = isSelected
        ) {
            Icon(
                imageVector = Icons.Edit,
                contentDescription = null,
                modifier = Modifier
                    .background(
                        lerp(color, MaterialTheme.colorScheme.surfaceVariant, 0.6f)
                    )
                    .padding(MaterialTheme.spacing.small),
                tint = lerp(MaterialTheme.colorScheme.onSurface, color, 0.2f)
            )
        }

    }
}

@Preview
@Composable
fun ColorItemPREVIEW() {
    Row {
        ColorItem(
            color = Color.Green,
            isSelected = true
        )
        ColorItem(
            color = Color.Blue,
            isSelected = true
        )
        ColorItem(
            color = Color.Red,
            isSelected = false
        )
    }
}