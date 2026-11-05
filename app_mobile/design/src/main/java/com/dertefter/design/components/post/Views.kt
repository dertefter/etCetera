package com.dertefter.design.components.post

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme

@Composable
fun Views(
    modifier: Modifier = Modifier,
    views: Int,
    isViewed: Boolean = false,
){

    PostActionButton(
        modifier = modifier,
        value = views,
        isChecked = isViewed,
        onClick = {},
        checkedContainerColor = Color.Transparent,
        checkedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        uncheckedContainerColor = Color.Transparent,
        uncheckedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        checkedIcon = Icons.VisibilityFilled,
        uncheckedIcon = Icons.Visibility
    )
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
