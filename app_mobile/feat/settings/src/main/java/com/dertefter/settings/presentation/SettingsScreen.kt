package com.dertefter.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.lists.SegmentedColumn
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

    val settingsSections = listOf(
        SettingsSection(
            title = stringResource(R.string.settings_section_account),
            settingsItems = listOf(
                SettingsItem(
                    title = stringResource(R.string.settings_account), icon = Icons.UserFilled, route = Routes.SettingsAccount
                ), SettingsItem(
                    title = stringResource(R.string.settings_billing),
                    icon = Icons.AccountBalanceWalletFilled,
                    route = Routes.Settings
                ), SettingsItem(
                    title = stringResource(R.string.settings_security), icon = Icons.Security, route = Routes.SettingsSecurity
                ), SettingsItem(
                    title = stringResource(R.string.settings_privacy), icon = Icons.DominoMaskFilled, route = Routes.Settings
                ),
            )
        ),
        SettingsSection(
            title = stringResource(R.string.settings_section_app),
            settingsItems = listOf(
                SettingsItem(
                    title = stringResource(R.string.settings_appearance), icon = Icons.PaletteFilled, route = Routes.SettingsTheme
                ),
                SettingsItem(
                    title = stringResource(R.string.settings_about), icon = Icons.InfoFilled, route = Routes.Settings
                )
            )
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

    val iconSourceColors = listOf(
        Color(0xFF00FF91),
        Color(0xFF005EFF),
        Color(0xFFC800FF),
        Color(0xFF1AFF00),
        Color(0xFFFFC800),
        Color(0xFFFF8C00),
        Color(0xFF00FFE1),
    )

    val iconColors = iconSourceColors.map {
        it.harmonize(MaterialTheme.colorScheme.onPrimaryContainer, true)
    }

    val iconBgColors = iconSourceColors.map {
        it.harmonize(MaterialTheme.colorScheme.primaryContainer, true)
    }

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
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
            contentPadding = contentPadding
        ) {

        itemsIndexed(settingsSections) { sectionIndex, section ->
             val sectionOffset = remember(sectionIndex) {
                 settingsSections.take(sectionIndex).sumOf { it.settingsItems.size }
             }

             SegmentedColumn(
                 title = section.title
             ) {
                     itemsIndexed(
                         items = section.settingsItems,
                         onClick = { _, item ->
                             onEvent(
                                 Event.OnNavigateTo(
                                     item.route
                                 )
                             )
                         }
                     ) { index, settingsItem ->
                         Row(
                             verticalAlignment = Alignment.CenterVertically,
                             horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                         ){
                             val globalIndex = sectionOffset + index
                             val iconColor = iconColors[globalIndex % iconColors.size]
                             val bgColor = iconBgColors[globalIndex % iconColors.size]
                             val shape = iconShapes[globalIndex % iconShapes.size]
                             Icon(
                                 settingsItem.icon,
                                 contentDescription = null,
                                 modifier = Modifier
                                     .size(36.dp)
                                     .clip(shape.toShape())
                                     .background(bgColor)
                                     .padding(MaterialTheme.spacing.medium),
                                 tint = iconColor
                             )

                             Text(
                                 settingsItem.title
                             )
                         }
                     }
                 }
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


