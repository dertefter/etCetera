package com.dertefter.settings_theme.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.spacing
import com.dertefter.settings_theme.R
import com.dertefter.settings_theme.presentation.Event
import com.dertefter.settings_theme.presentation.UiState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun AppBarThemingScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val hazeState = rememberHazeState()

    Scaffold(
        topBar = {
            AppTopBar(
                hazeState = hazeState,
                title = {
                    Text(text = stringResource(R.string.settings_theme_app_bar))
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


@Preview(showBackground = true)
@Composable
private fun AppBarThemingScreenPreview() {
    AppTheme(
        content = {
            AppBarThemingScreen(
                uiState = UiState(
                    emojiAvatarHarmonizeColor = EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER,
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
        },
    )
}
