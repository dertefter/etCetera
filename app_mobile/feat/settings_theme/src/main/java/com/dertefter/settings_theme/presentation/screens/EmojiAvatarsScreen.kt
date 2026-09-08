package com.dertefter.settings_theme.presentation.screens

import com.dertefter.settings_theme.presentation.Event
import com.dertefter.settings_theme.presentation.UiState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor
import com.dertefter.design.components.avatar.EmojiAvatar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.settings_theme.R
import com.gigamole.composefadingedges.horizontalFadingEdges

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EmojiAvatarsScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val (harmonizationColor, onHarmonizationColor) = uiState.emojiAvatarHarmonizeColor.getColors()

    val previewEmojiList = listOf("💙", "🦎", "🎁", "⚙️", "🥲", "🍃", "👽", "🦐")

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(text = stringResource(R.string.settings_theme_emoji_avatar))
                },
                navigationIcon = {
                    AppNavigationIcon(
                        icon = Icons.ArrowBack,
                        onClick = { onEvent(Event.OnNavigateBack) },
                        contentDescription = stringResource(com.dertefter.design.R.string.design_back_content_desc)
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        }) { contentPadding ->

        val contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + MaterialTheme.spacing.medium,
            bottom = contentPadding.calculateBottomPadding() + MaterialTheme.spacing.medium,
            start = contentPadding.calculateStartPadding(LocalLayoutDirection.current) + MaterialTheme.spacing.defaultScreenPadding,
            end = contentPadding.calculateEndPadding(LocalLayoutDirection.current) + MaterialTheme.spacing.defaultScreenPadding
        )

        LazyColumn(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
            contentPadding = contentPadding
        ) {

            item {
                Row(
                    modifier = Modifier
                        .horizontalFadingEdges(length = MaterialTheme.spacing.large)
                        .horizontalScroll(rememberScrollState())
                        .padding(
                            all = MaterialTheme.spacing.large
                        ),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    previewEmojiList.forEach {
                        EmojiAvatar(
                            emoji = it,
                            containerSize = 74.dp,
                            fontSize = 26.sp
                        )
                    }
                }
            }

            item {
                SegmentedColumn(
                    title = stringResource(R.string.settings_theme_emoji_harmonization)
                ){
                    item{
                        var menuExpanded by remember { mutableStateOf(false) }
                        Box {
                            Text(
                                text = uiState.emojiAvatarHarmonizeColor.getDisplayName(),
                                style = MaterialTheme.typography.labelMediumEmphasized,
                                color = onHarmonizationColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(harmonizationColor)
                                    .clickable { menuExpanded = true }
                                    .padding(MaterialTheme.spacing.large)
                                    .fillMaxWidth()
                            )
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                                shape = MaterialTheme.shapes.largeIncreased,
                            ) {
                                EmojiAvatarHarmonizationColor.entries.forEach { color ->
                                    val (itemBackgroundColor, itemContentColor) = color.getColors()
                                    DropdownMenuItem(
                                        text = { Text(color.getDisplayName()) },
                                        onClick = {
                                            onEvent(Event.OnUpdateEmojiAvatarHarmonizationColor(color))
                                            menuExpanded = false
                                        },
                                        modifier = Modifier
                                            .padding(horizontal = MaterialTheme.spacing.medium)
                                            .padding(bottom = MaterialTheme.spacing.small)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(itemBackgroundColor),
                                        colors = MenuDefaults.itemColors(
                                            textColor = itemContentColor,
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }

    }
}

@Composable
private fun EmojiAvatarHarmonizationColor.getDisplayName(): String {
    return when (this) {
        EmojiAvatarHarmonizationColor.PRIMARY -> stringResource(R.string.settings_theme_harmonization_primary)
        EmojiAvatarHarmonizationColor.SECONDARY -> stringResource(R.string.settings_theme_harmonization_secondary)
        EmojiAvatarHarmonizationColor.TERTIARY -> stringResource(R.string.settings_theme_harmonization_tertiary)
        EmojiAvatarHarmonizationColor.SURFACE_CONTAINER -> stringResource(R.string.settings_theme_harmonization_surface_container)
        EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER -> stringResource(R.string.settings_theme_harmonization_primary_container)
        EmojiAvatarHarmonizationColor.SECONDARY_CONTAINER -> stringResource(R.string.settings_theme_harmonization_secondary_container)
        EmojiAvatarHarmonizationColor.TERTIARY_CONTAINER -> stringResource(R.string.settings_theme_harmonization_tertiary_container)
    }
}

@Composable
private fun EmojiAvatarHarmonizationColor.getColors(): Pair<Color, Color> {
    val colorScheme = MaterialTheme.colorScheme
    return when (this) {
        EmojiAvatarHarmonizationColor.PRIMARY -> colorScheme.primary to colorScheme.onPrimary
        EmojiAvatarHarmonizationColor.SECONDARY -> colorScheme.secondary to colorScheme.onSecondary
        EmojiAvatarHarmonizationColor.TERTIARY -> colorScheme.tertiary to colorScheme.onTertiary
        EmojiAvatarHarmonizationColor.SURFACE_CONTAINER -> colorScheme.surfaceContainerHigh to colorScheme.onSurface
        EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER -> colorScheme.primaryContainer to colorScheme.onPrimaryContainer
        EmojiAvatarHarmonizationColor.SECONDARY_CONTAINER -> colorScheme.secondaryContainer to colorScheme.onSecondaryContainer
        EmojiAvatarHarmonizationColor.TERTIARY_CONTAINER -> colorScheme.tertiaryContainer to colorScheme.onTertiaryContainer
    }
}

@Preview(showBackground = true)
@Composable
private fun EmojiAvatarsScreenPreview() {
    AppTheme {
        EmojiAvatarsScreen(
            uiState = UiState(
                emojiAvatarHarmonizeColor = EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER,
                darkTheme = false,
                postHorizontalExtraSpace = true,
                postContained = true,
                postShowUsername = true,
                postSwapDateAndUsername = true
            ),
            onEvent = {}
        )
    }
}

