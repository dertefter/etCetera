package com.dertefter.settings_about.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.settings_about.R
import com.dertefter.settings_about.presentation.components.AppAboutHeader
import com.github.droibit.oss_licenses.ui.compose.material3.OssLicensesActivity


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsAboutScreen(
    onEvent: (Event) -> Unit,
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val titleAlpha = (scrollBehavior.state.overlappedFraction).coerceIn(0f, 1f)

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
                context.startActivity(OssLicensesActivity.createIntent(context))
            }
        ) ,
        Item(
            title = stringResource(R.string.settings_about_github_repo),
        action = {
            uriHandler.openUri("https://github.com/dertefter/etCetera")
        }
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
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
            item {
                AppAboutHeader(
                    appName = stringResource(R.string.settings_about_app_name),
                    desc = stringResource(R.string.settings_about_app_desc),
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
                )
            }

            item {
                SegmentedColumn(
                    title = stringResource(R.string.settings_about_itd_title)
                ) {
                    for (i in itdItems){
                        item(
                            onClick = i.action
                        ) {
                            Text(
                                i.title
                            )
                        }
                    }
                }
            }

            item {
                SegmentedColumn(
                    title = stringResource(R.string.settings_about_etcetera_title)
                ) {
                    for (i in etCeteraItems){
                        item(
                            onClick = i.action
                        ) {
                            Text(
                                i.title
                            )
                        }
                    }
                }
            }

        }
    }

}

@Preview()
@Composable
fun SettinsAboutScreenPrev() {
    AppTheme {
        SettingsAboutScreen(
            onEvent = {}
        )
    }
}




