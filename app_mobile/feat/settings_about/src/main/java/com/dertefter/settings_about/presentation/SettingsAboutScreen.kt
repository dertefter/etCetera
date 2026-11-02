package com.dertefter.settings_about.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.appbar.AppTopBarStyle
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.spacing
import com.dertefter.navigation.Routes
import com.dertefter.settings_about.R
import com.dertefter.settings_about.presentation.components.AppAboutHeader
import com.dertefter.settings_about.presentation.components.ChipItem
import com.materialkolor.ktx.harmonize
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun SettingsAboutScreen(
    onEvent: (Event) -> Unit,
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val titleAlpha = (scrollBehavior.state.overlappedFraction).coerceIn(0f, 1f)

    val hazeState = rememberHazeState()

    val packageManager = context.packageManager
    val packageInfo = remember(context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(context.packageName, 0)
            }
        } catch (_: Exception) {
            null
        }
    }
    val versionName = packageInfo?.versionName ?: "1.0"
    val versionCode = packageInfo?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            it.longVersionCode.toString()
        } else {
            @Suppress("DEPRECATION")
            it.versionCode.toString()
        }
    } ?: "1"

    val itdItems = listOf(
        Item(
            title = stringResource(R.string.settings_about_privacy_policy),
            action = {
                uriHandler.openUri("https://итд.com/privacy")
            }
        ),
        Item(
            title = stringResource(R.string.settings_about_terms_of_use),
            action = {
                uriHandler.openUri("https://итд.com/terms")
            }
        )
    )

    val etCeteraItems = listOf(
        Item(
            title = stringResource(R.string.settings_about_privacy_policy),
            action = {
                uriHandler.openUri("https://github.com/dertefter/etCetera/blob/9bebf9fd806f2d8ca638513a663938f349e0ca7a/PRIVACY.md")
            }
        ),
        Item(
            title = stringResource(R.string.settings_about_terms_of_use),
            action = {
                uriHandler.openUri("https://github.com/dertefter/etCetera/blob/9bebf9fd806f2d8ca638513a663938f349e0ca7a/TERMS.md")
            }
           ),
        Item(
            title = stringResource(R.string.settings_about_oss_licenses),
            action = {
                onEvent(Event.OnNavigateTo(Routes.Oss))
            }
        )
    )

    val versionCopiedText = stringResource(R.string.settings_about_version_copied)
    val buildCopiedText = stringResource(R.string.settings_about_build_number_copied)

    val chipItems = listOf(
        ChipItem(
            title = stringResource(R.string.settings_about_github_repo),
            icon = Icons.GitHub,
            color = Color(0xFFFF0000),
            action = {
                uriHandler.openUri("https://github.com/dertefter/etCetera")
            }
        ),
        ChipItem(
            title = stringResource(R.string.settings_about_telegram),
            icon = Icons.Telegram,
            color = Color(0xFF009EFF),
            action = {
                uriHandler.openUri("https://t.me/etcetera_app")
            }
        ),
        ChipItem(
            title = stringResource(R.string.settings_about_support),
            icon = Icons.VolunteerActivismFilled,
            color = Color(0xFF4CFF00),
            action = {
                uriHandler.openUri("https://dalink.to/dertefter")
            }
        ),
        ChipItem(
            title = stringResource(R.string.settings_about_tiktok),
            icon = Icons.TikTok,
            color = Color(0xFFFF00D5),
            action = {
                uriHandler.openUri("https://www.tiktok.com/@etcetera_app")
            }
        ),
        ChipItem(
            title = stringResource(R.string.settings_about_itd),
            icon = Icons.ITD,
            color = Color(0xFF0004FF),
            action = {
                uriHandler.openUri("https://итд.com/@etcetera_app")
            }
        ),
    )

    val appDebugItems = listOf(
        Item(
            title = stringResource(R.string.settings_about_app_version),
            subtitle = versionName,
            action = {
                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("App Version", versionName)
                @Suppress("UsePropertyAccessSyntax")
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(context, versionCopiedText, Toast.LENGTH_SHORT).show()
            }
        ),
        Item(
            title = stringResource(R.string.settings_about_build_number),
            subtitle = versionCode,
            action = {
                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Build Number", versionCode)
                @Suppress("UsePropertyAccessSyntax")
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(context, buildCopiedText, Toast.LENGTH_SHORT).show()
            }
        ),
        Item(
            title = stringResource(R.string.settings_about_crash_reports),
            action = {
                onEvent(Event.OnNavigateTo(Routes.CrashReports))
            }
        ),
    )

    Scaffold(
        topBar = {
            AppTopBar(
                hazeState = hazeState,
                appTopBarStyle = AppTopBarStyle.SMALL,
                title = {
                    Text(
                        text = stringResource(R.string.settings_about_app_name),
                        modifier = Modifier.alpha(titleAlpha)
                    )
                },
                subtitle = {
                    Text(
                        text = stringResource(R.string.settings_about_app_desc),
                        modifier = Modifier.alpha(titleAlpha)
                    )
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
        ) {
            item {
                AppAboutHeader(
                    appName = stringResource(R.string.settings_about_app_name),
                    desc = stringResource(R.string.settings_about_app_desc),
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                )
            }

            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        MaterialTheme.spacing.small
                    ),
                    contentPadding = PaddingValues(
                        horizontal = MaterialTheme.spacing.defaultScreenPadding
                    )
                ) {
                    items(chipItems){ chip ->
                        val containerColor = chip.color.harmonize(
                            MaterialTheme.colorScheme.primaryContainer,
                            matchSaturation = true
                        )
                        val labelColor = chip.color.harmonize(
                            MaterialTheme.colorScheme.onPrimaryContainer,
                            matchSaturation = true
                        )

                        val iconColor = chip.color.harmonize(
                            MaterialTheme.colorScheme.primary,
                            matchSaturation = true
                        )

                        SuggestionChip(
                            onClick = chip.action,
                            border = null,
                            label = { Text(chip.title) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = containerColor,
                                iconContentColor = iconColor,
                                labelColor = labelColor,
                            ),
                            icon = {
                                Icon(
                                    imageVector = chip.icon,
                                    modifier = Modifier.size(
                                        SuggestionChipDefaults.IconSize
                                    ),
                                    contentDescription = null
                                )
                            }
                        )

                    }

                }
            }

            item {
                SegmentedColumn(
                    title = stringResource(R.string.settings_about_itd_title),
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.defaultScreenPadding
                    )
                ) {
                    for ((title, _, action) in itdItems){
                        item(
                            onClick = action
                        ) {
                            Text(
                                title
                            )
                        }
                    }
                }
            }

            item {
                SegmentedColumn(
                    title = stringResource(R.string.settings_about_etcetera_title),
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.defaultScreenPadding
                    )
                ) {
                    for ((title, _, action) in etCeteraItems){
                        item(
                            onClick = action
                        ) {
                            Text(
                                title
                            )
                        }
                    }
                }
            }

            item {
                SegmentedColumn(
                    title = stringResource(R.string.settings_about_debug_title),
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.defaultScreenPadding
                    )
                ) {
                    for ((title, subtitle, action) in appDebugItems){
                        item(
                            onClick = action
                        ) {
                            Column{
                                Text(
                                    title
                                )
                                subtitle?.let {
                                    Text(
                                        text = subtitle,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }

                            }

                        }
                    }
                }
            }


        }
    }

}

@Preview
@Composable
fun SettinsAboutScreenPrev() {
    AppTheme {
        SettingsAboutScreen(
            onEvent = {}
        )
    }
}




