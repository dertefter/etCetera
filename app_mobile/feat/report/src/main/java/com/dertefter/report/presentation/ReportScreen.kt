package com.dertefter.report.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.components.appbar.AppToolbar
import com.dertefter.design.components.lists.segmentedListItemShapes
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

    val infiniteTransition = rememberInfiniteTransition(label = "AngleTransition")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing)
        ),
        label = "AngleAnimation"
    )

    val reasons = listOf(
        "spam" to R.string.report_reason_spam,
        "violence" to R.string.report_reason_violence,
        "harassment" to R.string.report_reason_hate,
        "nudity" to R.string.report_reason_adult,
        "misinformation" to R.string.report_reason_misinfo,
        "other" to R.string.report_reason_other
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            AppToolbar(
                title = stringResource(R.string.report_title),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
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
                        .padding(horizontal = MaterialTheme.spacing.extraLarge)
                        .padding(contentPadding)
                        .fillMaxSize()
                            .padding(MaterialTheme.spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    Icon(
                        imageVector = Icons.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(MaterialShapes.Cookie12Sided.toShape(startAngle = angle.toInt()))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(MaterialTheme.spacing.extraLarge)
                            .size(64.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
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
                    Column(
                        Modifier.selectableGroup(),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                    ) {
                        reasons.forEachIndexed { index, pair ->
                            val id = pair.first
                            val labelRes = pair.second
                            val selected = uiState.reason == id
                            ListItem(
                                selected = selected,
                                leadingContent = { RadioButton(selected = selected, onClick = null) },
                                onClick = { onEvent(Event.OnReasonSelected(id)) },
                                colors = ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                                shapes = segmentedListItemShapes(
                                    index = index,
                                    count = reasons.count(),
                                    selected = selected
                                )

                            ) {
                                Text(stringResource(labelRes))
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

@Preview(showBackground = false)
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
