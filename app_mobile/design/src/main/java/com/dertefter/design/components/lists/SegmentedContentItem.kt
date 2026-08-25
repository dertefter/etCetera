package com.dertefter.design.components.lists

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SegmentedContentItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    index: Int,
    count: Int,
    contentPadding: PaddingValues = PaddingValues(MaterialTheme.spacing.large),
    colors: ListItemColors = ListItemDefaults.segmentedColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ),
    content: @Composable () -> Unit
) {

    SegmentedListItem(
        modifier = modifier,
        onClick = onClick,
        shapes = segmentedListItemShapes(index, count),
        content = content,
        contentPadding = contentPadding,
        colors = colors

    )
}

@Composable
private fun segmentedListItemShapes(
    index: Int,
    count: Int
): ListItemShapes {
    val rounding = MaterialTheme.rounding
    val segmentRadius = rounding.small
    val edgeRadius = rounding.largeIncreased
    val interactiveRadius = rounding.largeIncreased

    return remember(index, count, segmentRadius, edgeRadius, interactiveRadius) {
        val topRadius = if (index == 0) edgeRadius else segmentRadius
        val bottomRadius = if (index == count - 1) edgeRadius else segmentRadius

        val interactiveTopRadius = if (index == 0) interactiveRadius else segmentRadius
        val interactiveBottomRadius = if (index == count - 1) interactiveRadius else segmentRadius

        ListItemShapes(
            shape = RoundedCornerShape(
                topStart = topRadius,
                topEnd = topRadius,
                bottomEnd = bottomRadius,
                bottomStart = bottomRadius
            ),
            selectedShape = RoundedCornerShape(
                topStart = interactiveTopRadius,
                topEnd = interactiveTopRadius,
                bottomEnd = interactiveBottomRadius,
                bottomStart = interactiveBottomRadius
            ),
            pressedShape = RoundedCornerShape(
                topStart = interactiveTopRadius,
                topEnd = interactiveTopRadius,
                bottomEnd = interactiveBottomRadius,
                bottomStart = interactiveBottomRadius
            ),
            focusedShape = RoundedCornerShape(
                topStart = interactiveTopRadius,
                topEnd = interactiveTopRadius,
                bottomEnd = interactiveBottomRadius,
                bottomStart = interactiveBottomRadius
            ),
            hoveredShape = RoundedCornerShape(
                topStart = interactiveTopRadius,
                topEnd = interactiveTopRadius,
                bottomEnd = interactiveBottomRadius,
                bottomStart = interactiveBottomRadius
            ),
            draggedShape = RoundedCornerShape(
                topStart = interactiveTopRadius,
                topEnd = interactiveTopRadius,
                bottomEnd = interactiveBottomRadius,
                bottomStart = interactiveBottomRadius
            )
        )
    }
}
