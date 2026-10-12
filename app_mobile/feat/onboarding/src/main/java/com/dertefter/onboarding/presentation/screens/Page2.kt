package com.dertefter.onboarding.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.spacing
import com.dertefter.onboarding.R
import com.github.droibit.oss_licenses.ui.compose.material3.OssLicensesActivity
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

private data class Item(
    val title: String,
    val action: () -> Unit = {}
)

@Composable
fun Page2() {

    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val hazeState = rememberHazeState()

    val itdItems = listOf(
        Item(
            title = stringResource(R.string.onboarding_privacy_policy),
            action = {
                uriHandler.openUri("https://итд.com/privacy")
            }
        ),
        Item(
            title = stringResource(R.string.onboarding_terms_of_use),
            action = {
                uriHandler.openUri("https://итд.com/terms")
            }
        )
    )

    val etCeteraItems = listOf(
        Item(
            title = stringResource(R.string.onboarding_privacy_policy),
            action = {
                uriHandler.openUri("https://github.com/dertefter/etCetera/blob/9bebf9fd806f2d8ca638513a663938f349e0ca7a/PRIVACY.md")
            }
        ),
        Item(
            title = stringResource(R.string.onboarding_terms_of_use),
            action = {
                uriHandler.openUri("https://github.com/dertefter/etCetera/blob/9bebf9fd806f2d8ca638513a663938f349e0ca7a/TERMS.md")
            }
        ),
        Item(
            title = stringResource(R.string.onboarding_oss_licenses),
            action = {
                context.startActivity(OssLicensesActivity.createIntent(context))
            }
        ),
        Item(
            title = stringResource(R.string.onboarding_github_repo),
            action = {
                uriHandler.openUri("https://github.com/dertefter/etCetera")
            }
        )
    )

    Scaffold(
        topBar = {
            AppTopBar(
                hazeState = hazeState,
                title = {
                    Text(stringResource(R.string.onboarding_page2_title))
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(
                    contentPadding
                            + PaddingValues(top = MaterialTheme.spacing.small)
                            + PaddingValues(horizontal = MaterialTheme.spacing.extraLarge)
                            + PaddingValues(bottom = MaterialTheme.bottomNavHeight)
                )
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                text = stringResource(R.string.onboarding_page2_subtitle)
            )

            SegmentedColumn(
                title = stringResource(R.string.onboarding_itd_title)
            ) {
                for ((title, action) in itdItems) {
                    item(
                        onClick = action
                    ) {
                        Text(title)
                    }
                }
            }

            SegmentedColumn(
                title = stringResource(R.string.onboarding_etcetera_title)
            ) {
                for ((title, action) in etCeteraItems) {
                    item(
                        onClick = action
                    ) {
                        Text(title)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Page2Preview() {
    AppTheme {
        Page2()
    }
}
