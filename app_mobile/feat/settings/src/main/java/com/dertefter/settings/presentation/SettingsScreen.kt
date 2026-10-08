package com.dertefter.settings.presentation

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.spacing
import com.dertefter.navigation.Routes
import com.dertefter.settings.R
import com.materialkolor.ktx.harmonize
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onEvent: (Event) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val context = LocalContext.current

    val settingsSections = listOf(
        SettingsSection(
            title = stringResource(R.string.settings_section_account),
            settingsItems = listOf(
                SettingsItem(
                    title = stringResource(R.string.settings_account),
                    icon = Icons.UserFilled,
                    onClick = { onEvent(Event.OnNavigateTo(Routes.SettingsAccount)) }
                ),
                //SettingsItem(
                //    title = stringResource(R.string.settings_billing),
                //    icon = Icons.AccountBalanceWalletFilled,
                //    onClick = { onEvent(Event.OnNavigateTo(Routes.Settings)) }
                //),
                SettingsItem(
                    title = stringResource(R.string.settings_security),
                    icon = Icons.Security,
                    onClick = { onEvent(Event.OnNavigateTo(Routes.SettingsSecurity)) }
                ),
                SettingsItem(
                    title = stringResource(R.string.settings_privacy),
                    icon = Icons.DominoMaskFilled,
                    onClick = { onEvent(Event.OnNavigateTo(Routes.SettingsPrivacy)) }
                ),
            )
        ),
        SettingsSection(
            title = stringResource(R.string.settings_section_app),
            settingsItems = listOf(
                SettingsItem(
                    title = stringResource(R.string.settings_appearance),
                    icon = Icons.PaletteFilled,
                    onClick = { onEvent(Event.OnNavigateTo(Routes.SettingsTheme)) }
                ),
                SettingsItem(
                    title = stringResource(R.string.settings_open_by_default),
                    icon = Icons.OpenInNew,
                    onClick = {

                        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            Intent(
                                Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                                "package:${context.packageName}".toUri()
                            )
                        } else {
                            Intent(
                                Settings.ACTION_APPLICATION_SETTINGS,
                                "package:${context.packageName}".toUri()
                            )
                        }

                        try {
                            context.startActivity(intent)
                        } catch (_: Throwable) {
                            Toast.makeText(
                                context,
                                R.string.settings_open_by_default_error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                ),
                SettingsItem(
                    title = stringResource(R.string.settings_about),
                    icon = Icons.InfoFilled,
                    onClick = { onEvent(Event.OnNavigateTo(Routes.SettingsAbout)) }
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
        MaterialShapes.Sunny,
        MaterialShapes.Cookie6Sided,
        MaterialShapes.Pentagon,
        MaterialShapes.Clover8Leaf,
    )

    val iconSourceColors = listOf(
        Color(0xFFE53935),
        Color(0xFF3949AB),
        Color(0xFFD81B60),
        Color(0xFF8E24AA),
        Color(0xFF00897B),
        Color(0xFFC0CA33),
        Color(0xFF43A047),
        Color(0xFF00ACC1),
        Color(0xFF19E6BD),
        Color(0xFF7CB342),

    )

    val iconColors = iconSourceColors.map {
        it.harmonize(MaterialTheme.colorScheme.onPrimary, true)
    }

    val iconBgColors = iconSourceColors.map {
        it.harmonize(MaterialTheme.colorScheme.primary, true)
    }

    val hazeState = rememberHazeState()

    Scaffold(
        topBar = {
            AppTopBar(
                hazeState = hazeState,
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

        itemsIndexed(settingsSections) { sectionIndex, section ->
             val sectionOffset = remember(sectionIndex) {
                 settingsSections.take(sectionIndex).sumOf { it.settingsItems.size }
             }

             SegmentedColumn(
                 title = section.title
             ) {
                     itemsIndexed(
                         items = section.settingsItems,
                         onClick = { _, item -> item.onClick() }
                     ) { index, settingsItem ->
                         Row(
                             verticalAlignment = Alignment.CenterVertically,
                             horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
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
                                 text = settingsItem.title,
                                 style = MaterialTheme.typography.titleMedium
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


