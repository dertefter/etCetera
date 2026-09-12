package com.dertefter.report.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.components.appbar.AppToolbar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.components.text_fields.TextFieldItem
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.report.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReportScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scrollState = rememberScrollState()

    val reasons = listOf<Pair<String, Int>>(
        "spam" to R.string.report_reason_spam,
        "violence" to R.string.report_reason_violence,
        "harassment" to R.string.report_reason_hate,
        "nudity" to R.string.report_reason_adult,
        "misinformation" to R.string.report_reason_misinfo,
        "other" to R.string.report_reason_other
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            AppToolbar(
                title = stringResource(R.string.report_title),
                navigationIcon = {
                    AppNavigationIcon(onClick = { onEvent(Event.OnNavigateBack) })
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { contentPadding ->
        AnimatedContent(
            targetState = uiState.isSuccess,
            label = "ReportContentAnimation"
        ) { isSuccess ->
            if (isSuccess) {
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize()
                        .padding(MaterialTheme.spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Check,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.report_success_title),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = MaterialTheme.spacing.medium),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.report_success_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = MaterialTheme.spacing.small),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = { onEvent(Event.OnNavigateBack) },
                        modifier = Modifier.padding(top = MaterialTheme.spacing.large)
                    ) {
                        Text(stringResource(R.string.report_action_ok))
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .verticalScroll(scrollState)
                        .padding(MaterialTheme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                ) {
                    Text(
                        text = stringResource(R.string.report_subtitle),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Column(Modifier.selectableGroup()) {
                        reasons.forEach { pair ->
                            val id = pair.first
                            val labelRes = pair.second
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (uiState.reason == id),
                                        onClick = { onEvent(Event.OnReasonSelected(id)) },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = MaterialTheme.spacing.small),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (uiState.reason == id),
                                    onClick = null
                                )
                                Text(
                                    text = stringResource(labelRes),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = MaterialTheme.spacing.medium)
                                )
                            }
                        }
                    }

                    TextFieldItem(
                        value = uiState.description,
                        onValueChange = { onEvent(Event.OnDescriptionChanged(it)) },
                        hint = stringResource(R.string.report_description_hint),
                        singleLine = false,
                        maxSymbols = 1000
                    )

                    AnimatedVisibility(visible = uiState.error != null) {
                        Text(
                            text = uiState.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = MaterialTheme.spacing.medium),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { onEvent(Event.OnNavigateBack) },
                            enabled = !uiState.isLoading
                        ) {
                            Text(stringResource(R.string.report_action_cancel))
                        }

                        Button(
                            onClick = { onEvent(Event.OnSubmitReport) },
                            enabled = !uiState.isLoading && uiState.reason != null,
                            modifier = Modifier.padding(start = MaterialTheme.spacing.small)
                        ) {
                            if (uiState.isLoading) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AppLoadingIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Text(
                                        text = stringResource(R.string.report_action_sending),
                                        modifier = Modifier.padding(start = MaterialTheme.spacing.small)
                                    )
                                }
                            } else {
                                Text(stringResource(R.string.report_action_send))
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
fun ReportScreenPreview() {
    AppTheme {
        ReportScreen(
            uiState = UiState(
                reason = "spam",
                description = "Some description"
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportScreenSuccessPreview() {
    AppTheme {
        ReportScreen(
            uiState = UiState(
                isSuccess = true
            ),
            onEvent = {}
        )
    }
}
