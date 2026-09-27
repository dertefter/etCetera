package com.dertefter.settings_theme.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.dertefter.navigation.Routes
import com.dertefter.settings_theme.R
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsThemeScreen(
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
                    item(
                        onClick = { onEvent(Event.OnNavigateTo(Routes.SettingsThemeNav)) }
                    ) {
                        Text(stringResource(R.string.settings_theme_nav_bar))
                    }
                    item(
                        onClick = { onEvent(Event.OnNavigateTo(Routes.SettingsThemeAppBar)) }
                    ) {
                        Text(stringResource(R.string.settings_theme_app_bar))
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
                postSwapDateAndUsername = true, true, true, true
            ),
            onEvent = {}
        )
    }
}

