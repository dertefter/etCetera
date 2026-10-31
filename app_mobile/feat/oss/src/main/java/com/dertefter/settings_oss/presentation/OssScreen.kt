package com.dertefter.settings_oss.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.appbar.AppTopBarStyle
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.common.ErrorCard
import com.dertefter.design.components.lists.SegmentedContentItem
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.spacing
import com.dertefter.oss.R
import com.github.droibit.oss_licenses.parser.OssLicense
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun OssScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val hazeState = rememberHazeState()
    var expandedIndex by remember { mutableStateOf<Int?>(null) }


    Scaffold(
        topBar = {
            AppTopBar(
                hazeState = hazeState,
                appTopBarStyle = AppTopBarStyle.SMALL,
                title = {
                    Text(
                        text = stringResource(R.string.settings_oss_title),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            when (uiState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AppLoadingIndicator()
                    }
                }

                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorCard(
                            title = stringResource(R.string.settings_oss_error),
                            message = uiState.message,
                            onRetry = { onEvent(Event.OnRetry) },
                        )
                    }
                }

                is UiState.Success -> {
                    if (uiState.licenses.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.settings_oss_empty),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .hazeSource(hazeState)
                                .nestedScroll(scrollBehavior.nestedScrollConnection)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                            contentPadding = PaddingValues(bottom = MaterialTheme.bottomNavHeight)
                                    + PaddingValues(vertical = MaterialTheme.spacing.medium)
                                    + PaddingValues(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                        ) {

                            itemsIndexed(uiState.licenses) { index, license ->
                                val isExpanded = expandedIndex == index
                                SegmentedContentItem(
                                    index = index,
                                    count = uiState.licenses.count(),
                                    onClick = {
                                        expandedIndex = index
                                    }
                                ) {

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateContentSize(),
                                    )
                                    {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Text(
                                                text = license.library,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f),
                                            )
                                            Icon(
                                                imageVector = if (isExpanded) Icons.ArrowDropUp else Icons.ArrowDropDown,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                        if (isExpanded) {
                                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                                            SelectionContainer {
                                                Text(
                                                    text = license.text,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        }
    }
}

@Preview
@Composable
fun OssScreenPrev() {
    AppTheme {
        OssScreen(
            uiState = UiState.Success(
                listOf(
                    OssLicense("androidx.compose.ui:ui", "Apache License 2.0..."),
                    OssLicense("kotlinx.coroutines", "Apache License 2.0...")
                )
            ),
            onEvent = {}
        )
    }
}
