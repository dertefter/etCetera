package com.dertefter.settings_theme.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.data.dto.app.EmojiAvatarHarmonizationColor
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.navigation.Routes
import com.dertefter.settings_theme.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsThemeScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
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

            item{
                SegmentedColumn(
                    title = stringResource(R.string.settings_theme_title)
                ) {
                    item(
                        onClick = { onEvent(Event.OnNavigateTo(Routes.SettingsThemeAvatars)) }
                    ) {
                        Text(stringResource(R.string.settings_theme_emoji_avatar))
                    }
                    item(
                        onClick = { onEvent(Event.OnNavigateTo(Routes.SettingsThemePosts)) }
                    ) {
                        Text(stringResource(R.string.settings_theme_posts_view))
                    }
                }
            }

        }

    }
}


@Preview(showBackground = true)
@Composable
private fun SettingsThemeScreenPreview() {
    AppTheme {
        SettingsThemeScreen(
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

