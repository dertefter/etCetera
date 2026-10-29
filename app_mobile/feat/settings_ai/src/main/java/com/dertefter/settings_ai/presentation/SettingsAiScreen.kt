package com.dertefter.settings_ai.presentation

import android.text.format.Formatter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.ai.model.AiError
import com.dertefter.ai.model.AiModel
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.common.ErrorCard
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing
import com.dertefter.settings_ai.R
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState


private val IMPORT_MIME_TYPES = arrayOf("*/*")

@Composable
fun SettingsAiScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val hazeState = rememberHazeState()

    val modelPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) onEvent(Event.OnImport(uri))
        }
    )

    Scaffold(
        topBar = {
            AppTopBar(
                hazeState = hazeState,
                title = {
                    Text(text = stringResource(R.string.settings_ai_title))
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
        }
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
                    + PaddingValues(horizontal = MaterialTheme.spacing.defaultScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            uiState.error?.let { error ->
                item {
                    ErrorCard(
                        title = stringResource(R.string.settings_ai_error_title),
                        message = stringResource(error.messageRes()),
                        onRetry = { onEvent(Event.OnErrorShown) }
                    )
                }
            }

            if (uiState.isLoading) {
                item {
                    AppLoadingIndicator(
                        modifier = Modifier.padding(MaterialTheme.spacing.large)
                    )
                }
            } else {
                item {
                    SegmentedColumn(
                        title = stringResource(R.string.settings_ai_models)
                    ) {
                        if (uiState.models.isEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.settings_ai_models_empty),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        itemsIndexed(
                            items = uiState.models,
                            onClick = { _, model -> onEvent(Event.OnSelect(model)) }
                        ) { _, model ->
                            ModelItem(
                                model = model,
                                isActive = model.id == uiState.activeModelId,
                                canDelete = !uiState.isImporting,
                                onDelete = { onEvent(Event.OnDeleteRequest(model)) }
                            )
                        }
                        item(
                            onClick = {
                                if (!uiState.isImporting) modelPicker.launch(IMPORT_MIME_TYPES)
                            }
                        ) {
                            ImportItem(isImporting = uiState.isImporting)
                        }
                    }
                }

                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.rounding.largeIncreased),
                        text = stringResource(R.string.settings_ai_import_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    uiState.pendingDelete?.let { model ->
        AlertDialog(
            onDismissRequest = { onEvent(Event.OnDeleteCancel) },
            title = {
                Text(text = stringResource(R.string.settings_ai_delete_title))
            },
            text = {
                Text(text = stringResource(R.string.settings_ai_delete_text, model.name))
            },
            confirmButton = {
                TextButton(onClick = { onEvent(Event.OnDeleteConfirm) }) {
                    Text(
                        text = stringResource(R.string.settings_ai_delete_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(Event.OnDeleteCancel) }) {
                    Text(text = stringResource(R.string.settings_ai_cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        )
    }
}

@Composable
private fun ModelItem(
    model: AiModel,
    isActive: Boolean,
    canDelete: Boolean,
    onDelete: () -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = model.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = Formatter.formatShortFileSize(context, model.sizeBytes),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isActive) {
            Icon(
                imageVector = Icons.Check,
                contentDescription = stringResource(R.string.settings_ai_active),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(
            onClick = onDelete,
            enabled = canDelete
        ) {
            Icon(
                imageVector = Icons.Delete,
                contentDescription = stringResource(R.string.settings_ai_delete),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun ImportItem(isImporting: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
    ) {
        if (isImporting) {
            AppLoadingIndicator(
                modifier = Modifier.size(24.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = stringResource(
                if (isImporting) R.string.settings_ai_importing else R.string.settings_ai_import
            ),
            style = MaterialTheme.typography.titleMedium,
            color = if (isImporting) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    }
}

private fun AiError.messageRes(): Int = when (this) {
    AiError.NotConfigured -> R.string.settings_ai_error_not_configured
    is AiError.ModelNotFound -> R.string.settings_ai_error_model_not_found
    is AiError.InvalidModel -> R.string.settings_ai_error_invalid_model
    is AiError.ImportFailed -> R.string.settings_ai_error_import_failed
    is AiError.DeleteFailed -> R.string.settings_ai_error_delete_failed
    else -> R.string.settings_ai_error_unexpected
}

@Preview(showBackground = true)
@Composable
private fun SettingsAiScreenPreview() {
    AppTheme {
        SettingsAiScreen(
            uiState = UiState(
                isLoading = false,
                models = listOf(
                    AiModel(
                        id = "gemma-3n-e2b.litertlm",
                        name = "gemma-3n-e2b",
                        path = "",
                        sizeBytes = 2_400_000_000,
                        importedAt = 1_760_000_000_000
                    ),
                    AiModel(
                        id = "qwen-2.5-0.5b.litertlm",
                        name = "qwen-2.5-0.5b",
                        path = "",
                        sizeBytes = 540_000_000,
                        importedAt = 1_760_000_000_000
                    )
                ),
                activeModelId = "gemma-3n-e2b.litertlm"
            ),
            onEvent = {}
        )
    }
}
