package com.dertefter.settings_security.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.dertefter.data.dto.auth.AuthSessionDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.settings_security.R
import com.dertefter.settings_security.presentation.component.SessionDialogContent
import com.dertefter.settings_security.presentation.component.SessionItemContent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsSecurityScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()
    var selectedSession by remember { mutableStateOf<AuthSessionDto?>(null) }

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
                        Text(text = stringResource(R.string.settings_security_title))
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
            floatingActionButton = {
                AnimatedVisibility(
                    visible = uiState.sessions != null && uiState.sessions.count() > 1
                ) {
                    ExtendedFloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        onClick = {
                            onEvent(Event.OnDeleteAllSessions)
                        }
                    ) {
                        Text(
                            stringResource(R.string.settings_security_clear_sessions)
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center


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
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                contentPadding = combinedPadding,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                uiState.sessions?.let { sessions ->
                    item {
                        SegmentedColumn(
                            title = stringResource(R.string.settings_security_sessions),
                            content = {
                                items(sessions, onClick = { session ->
                                    selectedSession = session
                                }) { session ->
                                    SessionItemContent(session = session)
                                }
                            }
                        )
                    }
                } ?:  item {
                    AppLoadingIndicator(
                        modifier = Modifier.padding(MaterialTheme.spacing.large)
                    )
                }
            }
        }
    }

    if (selectedSession != null) {
        val session = selectedSession!!
        val deviceName = session.deviceModel ?: session.clientName ?: stringResource(R.string.settings_security_unknown_device)

        AlertDialog(
            onDismissRequest = { selectedSession = null },
            title = {
                Text(text = deviceName)
            },
            text = {
                SessionDialogContent(session = session)
            },
            confirmButton = {
                if (!session.isCurrent) {
                    TextButton(
                        onClick = {
                            onEvent(Event.OnDeleteSession(session.id))
                            selectedSession = null
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.settings_security_terminate_session),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedSession = null }) {
                    Text(text = stringResource(R.string.settings_security_close))
                }
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = true,
                decorFitsSystemWindows = true
            ),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun SettingsSecurityScreenPreview() {
    AppTheme {
        SettingsSecurityScreen(
            uiState = UiState(
                isLoading = false,
                sessions = listOf(
                    AuthSessionDto(
                        id = "1",
                        isCurrent = true,
                        createdAt = "2024-01-01T00:00:00Z",
                        lastUsedAt = "2024-01-01T00:00:00Z",
                        expiresAt = "2025-01-01T00:00:00Z",
                        ipAddress = "192.168.1.1",
                        ipCountry = "Russia",
                        ipCity = "Moscow",
                        deviceType = "Mobile",
                        osName = "Android",
                        osVersion = "14",
                        clientName = "etCetera",
                        clientVersion = "1.0.0",
                        deviceModel = "Pixel 8"
                    ),
                )
            ),
            onEvent = {}
        )
    }
}
