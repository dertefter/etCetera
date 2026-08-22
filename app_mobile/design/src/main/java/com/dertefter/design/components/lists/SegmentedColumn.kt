package com.dertefter.design.components.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing

interface SegmentedColumnScope {
    fun item(
        onClick: () -> Unit = {},
        itemInnerPadding: PaddingValues? = null,
        content: @Composable () -> Unit
    )

    fun <T> items(
        items: List<T>,
        onClick: (T) -> Unit = {},
        itemInnerPadding: PaddingValues? = null,
        itemContent: @Composable (T) -> Unit
    )

    fun <T> itemsIndexed(
        items: List<T>,
        onClick: (Int, T) -> Unit = { _, _ -> },
        itemInnerPadding: PaddingValues? = null,
        itemContent: @Composable (Int, T) -> Unit
    )
}

private class SegmentedColumnScopeImpl : SegmentedColumnScope {
    val listItems = mutableListOf<Triple<() -> Unit, @Composable () -> Unit, PaddingValues?>>()

    override fun item(
        onClick: () -> Unit,
        itemInnerPadding: PaddingValues?,
        content: @Composable () -> Unit
    ) {
        listItems.add(Triple(onClick, content, itemInnerPadding))
    }

    override fun <T> items(
        items: List<T>,
        onClick: (T) -> Unit,
        itemInnerPadding: PaddingValues?,
        itemContent: @Composable (T) -> Unit
    ) {
        items.forEach { data ->
            item(
                onClick = { onClick(data) },
                content = { itemContent(data) },
                itemInnerPadding = itemInnerPadding
            )
        }
    }

    override fun <T> itemsIndexed(
        items: List<T>,
        onClick: (Int, T) -> Unit,
        itemInnerPadding: PaddingValues?,
        itemContent: @Composable (Int, T) -> Unit
    ) {
        items.forEachIndexed { index, data ->
            item(
                onClick = { onClick(index, data) },
                content = { itemContent(index, data) },
                itemInnerPadding = itemInnerPadding
            )
        }
    }
}

@Composable
fun SegmentedColumn(
    modifier: Modifier = Modifier,
    itemInnerPadding: PaddingValues = PaddingValues(MaterialTheme.spacing.large),
    content: SegmentedColumnScope.() -> Unit
) {
    SegmentedColumnImpl(
        modifier = modifier,
        title = null,
        itemInnerPadding = itemInnerPadding,
        content = content
    )
}

@Composable
fun SegmentedColumn(
    modifier: Modifier = Modifier,
    title: String,
    itemInnerPadding: PaddingValues = PaddingValues(MaterialTheme.spacing.large),
    content: SegmentedColumnScope.() -> Unit
) {
    SegmentedColumnImpl(
        modifier = modifier,
        title = title,
        itemInnerPadding = itemInnerPadding,
        content = content
    )
}

@Composable
private fun SegmentedColumnImpl(
    modifier: Modifier = Modifier,
    title: String? = null,
    itemInnerPadding: PaddingValues = PaddingValues(MaterialTheme.spacing.large),
    content: SegmentedColumnScope.() -> Unit
) {
    val scope = remember { SegmentedColumnScopeImpl() }
    scope.listItems.clear()
    scope.content()

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        if (title != null) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.rounding.largeIncreased)
                    .padding(bottom = MaterialTheme.spacing.extraSmall),
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMediumEmphasized,
                color = MaterialTheme.colorScheme.primary
            )
        }

        scope.listItems.forEachIndexed { index, (itemOnClick, itemContent, perItemPadding) ->
            SegmentedContentItem(
                onClick = itemOnClick,
                index = index,
                count = scope.listItems.size,
                content = itemContent,
                contentPadding = perItemPadding ?: itemInnerPadding
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SegmentedColumnPreview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            SegmentedColumn(
                title = "With Title",
                content = {
                    val list = listOf("Item 1", "Item 2", "Item 3")
                    items(list) { text ->
                        Text(
                            text = text,
                            modifier = Modifier.fillMaxWidth().background(Color.Blue)
                        )
                    }
                }
            )

            SegmentedColumn(
                content = {
                    item {
                        Text(
                            text = "Without Title",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    }
}
