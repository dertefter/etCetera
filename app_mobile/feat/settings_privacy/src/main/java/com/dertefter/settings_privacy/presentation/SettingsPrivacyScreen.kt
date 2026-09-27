package com.dertefter.settings_privacy.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.data.dto.me.PrivacyDto
import com.dertefter.data.dto.user.VisibilityDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.spacing
import com.dertefter.settings_privacy.R
import com.dertefter.settings_privacy.presentation.component.SwitchItem
import com.dertefter.settings_privacy.presentation.component.VisibilityItem
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsPrivacyScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()

    val hazeState = rememberHazeState()

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = uiState.isLoading,
        onRefresh = {
            onEvent(Event.OnRefresh)
        },
        indicator = {
            PullToRefreshIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState,
                isRefreshing = uiState.isLoading
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    hazeState = hazeState,
                    title = {
                        Text(text = stringResource(R.string.settings_privacy_title))
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
            },


        ) { contentPadding ->

            LazyColumn(
                modifier = Modifier
                    .hazeSource(hazeState)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
                contentPadding = contentPadding
                        + PaddingValues(bottom = MaterialTheme.bottomNavHeight)
                        + PaddingValues(vertical = MaterialTheme.spacing.medium)
                        + PaddingValues(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                uiState.privacy?.let { privacy ->
                    item {
                        SegmentedColumn(
                            title = stringResource(R.string.settings_privacy_account_section)
                        ) {
                            item {
                                SwitchItem(
                                    title = stringResource(R.string.settings_privacy_private_account),
                                    description = stringResource(R.string.settings_privacy_private_account_desc),
                                    checked = privacy.isPrivate,
                                    onCheckedChange = { onEvent(Event.ChangeIsPrivate(it)) }
                                )
                            }
                        }
                    }

                    item {
                        SegmentedColumn(
                            title = stringResource(R.string.settings_privacy_visibility_section)
                        ) {
                            item {
                                VisibilityItem(
                                    title = stringResource(R.string.settings_privacy_wall_access),
                                    description = stringResource(R.string.settings_privacy_wall_access_desc),
                                    value = privacy.wallAccess,
                                    onValueChange = { onEvent(Event.ChangeWallAccess(it)) }
                                )
                            }
                            item {
                                VisibilityItem(
                                    title = stringResource(R.string.settings_privacy_likes_visibility),
                                    description = stringResource(R.string.settings_privacy_likes_visibility_desc),
                                    value = privacy.likesVisibility,
                                    onValueChange = { onEvent(Event.ChangeLikesVisibility(it)) }
                                )
                            }
                            item {
                                VisibilityItem(
                                    title = stringResource(R.string.settings_privacy_message_access),
                                    description = stringResource(R.string.settings_privacy_message_access_desc),
                                    value = privacy.messageAccess,
                                    onValueChange = { onEvent(Event.ChangeMessageAccess(it)) }
                                )
                            }
                            item {
                                SwitchItem(
                                    title = stringResource(R.string.settings_privacy_show_last_seen),
                                    description = stringResource(R.string.settings_privacy_show_last_seen_desc),
                                    checked = privacy.showLastSeen,
                                    onCheckedChange = { onEvent(Event.ChangeShowLastSeen(it)) }
                                )
                            }
                        }
                    }
                }

            }

        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SettingsPrivacyScreenPreview() {
    AppTheme {
        SettingsPrivacyScreen(
            uiState = UiState(
                isLoading = false,
                privacy = PrivacyDto(
                    isPrivate = false,
                    wallAccess = VisibilityDto.EVERYONE,
                    likesVisibility = VisibilityDto.FOLLOWERS,
                    messageAccess = VisibilityDto.MUTUAL,
                    showLastSeen = true
                )
            ),
            onEvent = {}
        )
    }
}
