package com.dertefter.settings_theme.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.dertefter.settings_theme.R
import com.dertefter.settings_theme.presentation.Event
import com.dertefter.settings_theme.presentation.UiState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavThemingScreen(
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
                    Text(text = stringResource(R.string.settings_theme_nav_bar))
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

    }
}


@Preview(showBackground = true)
@Composable
private fun NavThemingScreenPreview() {
    AppTheme(
        content = {
            NavThemingScreen(
                uiState = UiState(
                    emojiAvatarHarmonizeColor = EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER,
                    darkTheme = false,
                    postHorizontalExtraSpace = true,
                    postContained = true,
                    postShowUsername = true,
                    postSwapDateAndUsername = true,
                    navFloating = true,
                    navLabeled = true,
                    navBlurred = true
                ),
                onEvent = {}
            )
        },
    )
}
