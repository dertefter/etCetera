package com.dertefter.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.navigation.Routes
import com.dertefter.settings.R
import com.materialkolor.ktx.harmonize

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onEvent: (Event) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val settingsItems = listOf(
        SettingsItem(
            titleRes = R.string.settings_account, icon = Icons.UserFilled, route = Routes.Auth
        ), SettingsItem(
            titleRes = R.string.settings_billing,
            icon = Icons.AccountBalanceWalletFilled,
            route = Routes.Auth
        ), SettingsItem(
            titleRes = R.string.settings_security, icon = Icons.Security, route = Routes.Auth
        ), SettingsItem(
            titleRes = R.string.settings_privacy, icon = Icons.DominoMaskFilled, route = Routes.Auth
        ), SettingsItem(
            titleRes = R.string.settings_about, icon = Icons.InfoFilled, route = Routes.Auth
        )

    )

    val iconShapes = listOf(
        MaterialShapes.Gem,
        MaterialShapes.Pill,
        MaterialShapes.Cookie4Sided,
        MaterialShapes.Slanted,
        MaterialShapes.Cookie9Sided,
        MaterialShapes.PixelCircle,
        MaterialShapes.Cookie6Sided,
        MaterialShapes.Pentagon,
        MaterialShapes.Sunny,
        MaterialShapes.Clover8Leaf,
    )

    val iconColors = listOf(
        Color(0xFF00FF91),
        Color(0xFF005EFF),
        Color(0xFFC800FF),
        Color(0xFF1AFF00),
        Color(0xFFFFC800),
        Color(0xFFFF8C00),
        Color(0xFF00FFE1),
    )

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(text = stringResource(R.string.settings_title))
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
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            contentPadding = contentPadding
        ) {

            itemsIndexed(
                items = settingsItems
            ) { index, item ->
                SegmentedListItem(
                    onClick = {

                    },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    shapes = ListItemDefaults.segmentedShapes(
                        index = index,
                        count = settingsItems.count(),
                    ),
                    leadingContent = {
                        Icon(
                            item.icon,
                            contentDescription = null,
                            modifier = Modifier
                                .clip(iconShapes[index % iconShapes.size].toShape())
                                .background(
                                    iconColors[index % iconColors.size].harmonize(
                                        MaterialTheme.colorScheme.primary, true
                                    )
                                )
                                .padding(MaterialTheme.spacing.medium)
                                .size(22.dp),
                            tint = iconColors[index % iconColors.size].harmonize(
                                MaterialTheme.colorScheme.onPrimary, true
                            )
                        )
                    },
                    supportingContent = {
                        item.subtitleRes?.let { subtitleRes ->
                            Text(stringResource(subtitleRes))
                        }

                    },
                    content = {
                        Text(
                            stringResource(item.titleRes)
                        )
                    },
                )
            }

        }

    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    AppTheme {
        SettingsScreen(
            onEvent = {})
    }
}


