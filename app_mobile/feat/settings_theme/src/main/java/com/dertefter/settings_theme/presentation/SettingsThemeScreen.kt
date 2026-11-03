package com.dertefter.settings_theme.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.avatar.Avatar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.isBlurSupported
import com.dertefter.design.theme.spacing
import com.dertefter.settings_theme.R
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun SettingsThemeScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val hazeState = rememberHazeState()

    val isBlurSupported = MaterialTheme.isBlurSupported

    // Цвет гармонизации аватаров и цвет текста поверх него
    val (harmonizationColor, onHarmonizationColor) = uiState.emojiAvatarHarmonizeColor.getColors()

    // Набор эмодзи для превью аватаров
    val previewEmojiList = listOf("💙", "🦎", "🎁", "⚙️", "🥲", "🍃", "👽", "🦐")

    Scaffold(
        topBar = {

            AppTopBar(
                hazeState = hazeState,
                title = {
                    Text(text = stringResource(R.string.settings_theme_title))
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

        LazyColumn(
            modifier = Modifier
                .hazeSource(hazeState)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
            contentPadding = contentPadding
                    + PaddingValues(bottom = MaterialTheme.bottomNavHeight)
                    + PaddingValues(vertical = MaterialTheme.spacing.medium)
                    + PaddingValues(horizontal = MaterialTheme.spacing.defaultScreenPadding)
        ) {

            item{
                SegmentedColumn(
                    title = stringResource(R.string.settings_theme_color_scheme)
                ){
                    item{
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ){
                            ToggleButton(
                                checked = uiState.darkTheme == false,
                                onCheckedChange = {
                                    onEvent(Event.OnUpdateDarkTheme(false))
                                },
                                modifier = Modifier.weight(1f)
                            ){
                                Text(stringResource(R.string.settings_theme_day))
                            }
                            ToggleButton(
                                checked = uiState.darkTheme == true,
                                onCheckedChange = {
                                    onEvent(Event.OnUpdateDarkTheme(true))
                                },
                                modifier = Modifier.weight(1f)
                            ){
                                Text(stringResource(R.string.settings_theme_night))
                            }
                            ToggleButton(
                                checked = uiState.darkTheme == null,
                                onCheckedChange = {
                                    onEvent(Event.OnUpdateDarkTheme(null))
                                },
                                modifier = Modifier.weight(1f)
                            ){
                                Text(stringResource(R.string.settings_theme_auto))
                            }
                        }
                    }
                }
            }


            item {
                SegmentedColumn(
                    title = stringResource(R.string.settings_theme_emoji_avatar)
                ) {

                    item(
                        itemInnerPadding = PaddingValues()
                    ) {
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(
                                    all = MaterialTheme.spacing.large
                                ),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                        ) {
                            previewEmojiList.forEach {
                                Avatar(
                                    data = it,
                                    containerSize = 68.dp
                                )
                            }
                        }
                    }

                    item{
                        var menuExpanded by remember { mutableStateOf(false) }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = stringResource(R.string.settings_theme_emoji_harmonization),
                                modifier = Modifier.weight(1f)
                            )

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

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                        ) {
                            Text(
                                text = stringResource(R.string.settings_theme_emoji_animated),
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = uiState.animatedEmoji,
                                onCheckedChange = { onEvent(Event.OnUpdateAnimatedEmoji(it)) }
                            )
                        }
                    }

                }
            }

            item(key = "post_settings") {
                SegmentedColumn(
                    title = stringResource(R.string.settings_theme_posts_appearance),
                    modifier = Modifier
                        .animateItem()
                ) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                        ){
                            Text(
                                stringResource(R.string.settings_theme_post_containers),
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = uiState.postContained,
                                onCheckedChange = { onEvent(Event.OnUpdatePostContained(it)) }
                            )
                        }
                    }

                    if (uiState.postContained){
                        item {
                            Row(
                                modifier = Modifier.animateItem(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                            ){
                                Text(
                                    stringResource(R.string.settings_theme_post_horizontal_padding),
                                    modifier = Modifier.weight(1f)
                                )
                                Switch(
                                    checked = uiState.postHorizontalExtraSpace,
                                    onCheckedChange = { onEvent(Event.OnUpdatePostHorizontalExtraSpace(it)) }
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                        ){
                            Text(
                                stringResource(R.string.settings_theme_post_show_username),
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = uiState.postShowUsername,
                                onCheckedChange = { onEvent(Event.OnUpdatePostShowUsername(it)) }
                            )
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                        ){
                            Text(
                                stringResource(R.string.settings_theme_post_swap_date_username),
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = uiState.postSwapDateAndUsername,
                                onCheckedChange = { onEvent(Event.OnUpdatePostSwapDateAndUsername(it)) }
                            )
                        }
                    }
                }
            }

            item {
                SegmentedColumn(
                    title = stringResource(R.string.settings_theme_nav_bar_style)
                ){
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                        ){
                            Text(
                                text = stringResource(R.string.settings_theme_nav_floating),
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = uiState.navFloating,
                                onCheckedChange = { onEvent(Event.OnUpdateNavFloating(it)) }
                            )
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                        ){
                            Text(
                                text = stringResource(R.string.settings_theme_nav_labeled),
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = uiState.navLabeled,
                                onCheckedChange = { onEvent(Event.OnUpdateNavLabeled(it)) }
                            )
                        }
                    }

                    if (isBlurSupported){
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                            ){
                                Text(
                                    text = stringResource(R.string.settings_theme_nav_blurred),
                                    modifier = Modifier.weight(1f)
                                )
                                Switch(
                                    checked = uiState.navBlurred,
                                    onCheckedChange = { onEvent(Event.OnUpdateNavBlurred(it)) }
                                )
                            }
                        }
                    }
                }
            }

            item {
                SegmentedColumn(
                    title = stringResource(R.string.settings_theme_app_bar_style)
                ) {

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                        ) {
                            Text(
                                text = stringResource(R.string.settings_theme_app_bar_background),
                                modifier = Modifier.weight(1f)
                            )

                            var menuExpanded by remember { mutableStateOf(false) }

                            Box {
                                Text(
                                    text = when (uiState.appBarFaded) {
                                        true -> stringResource(R.string.settings_theme_app_bar_background_fade)
                                        false -> stringResource(R.string.settings_theme_app_bar_background_solid)
                                        null -> stringResource(R.string.settings_theme_app_bar_background_auto)
                                    },
                                    style = MaterialTheme.typography.labelLargeEmphasized,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier
                                        .padding(vertical = MaterialTheme.spacing.small)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .clickable { menuExpanded = true }
                                        .padding(
                                            vertical = MaterialTheme.spacing.medium,
                                            horizontal = MaterialTheme.spacing.large
                                        )
                                )

                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false },
                                    shape = MaterialTheme.shapes.largeIncreased,
                                ) {
                                    val options = listOf(
                                        null to stringResource(R.string.settings_theme_app_bar_background_auto),
                                        true to stringResource(R.string.settings_theme_app_bar_background_fade),
                                        false to stringResource(R.string.settings_theme_app_bar_background_solid)
                                    )
                                    options.forEach { (value, label) ->
                                        DropdownMenuItem(
                                            text = { Text(label) },
                                            onClick = {
                                                onEvent(Event.OnUpdateAppBarFaded(value))
                                                menuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isBlurSupported){
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                            ) {
                                Text(
                                    text = stringResource(R.string.settings_theme_app_bar_blurred),
                                    modifier = Modifier.weight(1f)
                                )
                                Switch(
                                    checked = uiState.appBarBlurred,
                                    onCheckedChange = { onEvent(Event.OnUpdateAppBarBlurred(it)) }
                                )
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
        EmojiAvatarHarmonizationColor.DEFAULT -> stringResource(R.string.settings_theme_harmonization_default)
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
        EmojiAvatarHarmonizationColor.DEFAULT -> colorScheme.surfaceVariant to colorScheme.onSurfaceVariant
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
private fun SettingsThemeScreenPreview() {
    AppTheme {
        SettingsThemeScreen(
            uiState = UiState(
                emojiAvatarHarmonizeColor = EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER,
                animatedEmoji = true,
                darkTheme = false,
                postHorizontalExtraSpace = true,
                postContained = true,
                postShowUsername = true,
                postSwapDateAndUsername = true,
                navFloating = true,
                navLabeled = true,
                navBlurred = true,
                appBarBlurred = true,
                appBarFaded = null
            ),
            onEvent = {}
        )
    }
}
