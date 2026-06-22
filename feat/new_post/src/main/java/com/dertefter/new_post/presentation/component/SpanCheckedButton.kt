package com.dertefter.new_post.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme

@Composable
fun SpanCheckButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onClick: () -> Unit = {},
    isChecked: Boolean = false
){

    val containerSize  = 40.dp
    val cornerRadius by animateDpAsState(
        if (isChecked) containerSize / 2 else containerSize / 4
    )

    val containerColor by animateColorAsState(
        if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    )

    Surface(
        modifier = modifier
            .size(containerSize),
        color = containerColor,
        shape = RoundedCornerShape(cornerRadius),
        onClick = onClick,
    ) {

        Icon(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize(),
            contentDescription = null,
            imageVector = icon,
        )

    }
}

@Preview
@Composable
fun SpanCheckButtonPreview() {
    AppTheme {
        SpanCheckButton(
            icon = Icons.FormatBold,
            isChecked = false
        )
    }
}

@Preview
@Composable
fun SpanCheckButtonCheckedPreview() {
    AppTheme {
        SpanCheckButton(
            icon = Icons.FormatBold,
            isChecked = true
        )
    }
}
