package com.dertefter.settings_privacy.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.data.dto.me.PrivacyDto
import com.dertefter.data.dto.user.VisibilityDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.settings_privacy.R
import com.dertefter.settings_privacy.presentation.component.SwitchItem
import com.dertefter.settings_privacy.presentation.component.VisibilityItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsPrivacyScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()

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
                LargeFlexibleTopAppBar(
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

            val combinedPadding = PaddingValues(
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
                contentPadding = combinedPadding,
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
