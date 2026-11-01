package com.dertefter.banner_edit.presentation

import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.banner_edit.R
import com.dertefter.banner_edit.domain.BannerLimits
import com.dertefter.banner_edit.presentation.drawbox.DrawBox
import com.dertefter.banner_edit.presentation.drawbox.DrawController
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.components.loading.Shimmer
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun BannerEditScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val drawController = remember { DrawController() }
    val scrollState = rememberScrollState()

    val color by drawController.color.collectAsState()
    val strokeWidth by drawController.strokeWidth.collectAsState()

    val uploading = uiState.uploadStatus == UploadStatus.UPLOADING
    val importing = uiState.importStage != null
    val busy = uploading || importing

    val canUndo = drawController.canUndo
    val canRedo = drawController.canRedo

    LaunchedEffect(busy) {
        drawController.enabled.value = !busy
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) onEvent(Event.OnImportImage(uri))
    }

    uiState.importedStrokes?.let { imported ->
        if (!canUndo) {
            drawController.loadStrokes(imported)
            onEvent(Event.OnAcceptImport(imported))
        } else {
            AlertDialog(
                onDismissRequest = { onEvent(Event.OnRejectImport) },
                title = { Text(stringResource(R.string.banner_edit_import_replace_title)) },
                text = { Text(stringResource(R.string.banner_edit_import_replace_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        drawController.loadStrokes(imported)
                        onEvent(Event.OnAcceptImport(imported))
                    }) {
                        Text(stringResource(R.string.banner_edit_import_replace_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onEvent(Event.OnRejectImport) }) {
                        Text(stringResource(R.string.banner_edit_import_replace_cancel))
                    }
                }
            )
        }
    }

    if (uiState.importFailed) {
        AlertDialog(
            onDismissRequest = { onEvent(Event.OnRejectImport) },
            title = { Text(stringResource(R.string.banner_edit_import_error_title)) },
            text = { Text(stringResource(R.string.banner_edit_import_error_message)) },
            confirmButton = {
                TextButton(onClick = { onEvent(Event.OnRejectImport) }) {
                    Text(stringResource(R.string.banner_edit_import_error_ok))
                }
            }
        )
    }

    val hazeState = rememberHazeState()

    Scaffold(
        topBar = {
            AppTopBar(
                hazeState = hazeState,
                title = {
                    Text(stringResource(R.string.banner_edit_title))
                },
                navigationIcon = {
                    AppNavigationIcon(
                        onClick = { onEvent(Event.OnBack) }
                    )
                },
                actions = {



                    if (importing) {
                        AppNavigationIcon(
                            onClick = { onEvent(Event.OnCancelImport) },
                            icon = Icons.Close,
                            contentDescription = stringResource(R.string.banner_edit_import_cancel),
                        )
                    }
                    AppNavigationIcon(
                        onClick = { drawController.undo() },
                        icon = Icons.Undo,
                        enabled = canUndo && !busy,
                        contentDescription = stringResource(R.string.banner_edit_undo),
                    )
                    AppNavigationIcon(
                        onClick = { drawController.redo() },
                        icon = Icons.Redo,
                        enabled = canRedo && !busy,
                        contentDescription = stringResource(R.string.banner_edit_redo),
                    )
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .padding(contentPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(MaterialTheme.spacing.defaultScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(BannerLimits.W.toFloat() / BannerLimits.H.toFloat())
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.TopStart
            ) {
                DrawBox(
                    controller = drawController,
                    modifier = Modifier.fillMaxSize()
                )

                this@Column.AnimatedVisibility(
                    modifier = Modifier.fillMaxSize(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                    visible = uploading
                ) {
                    Shimmer(
                        modifier = Modifier
                            .clickable(onClick = {})
                            .fillMaxSize(),
                        baseColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                        highlightColor = MaterialTheme.colorScheme.primaryContainer,
                        scale = 1.2f
                    )
                    AppLoadingIndicator(
                        modifier = Modifier.padding(60.dp)
                    )
                }


                this@Column.AnimatedVisibility(
                    modifier = Modifier.fillMaxSize(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                    visible = importing
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Shimmer(
                            modifier = Modifier
                                .clickable(onClick = {})
                                .fillMaxSize(),
                            baseColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                            highlightColor = MaterialTheme.colorScheme.primaryContainer,
                            scale = 1.2f
                        )

                        Column(
                            modifier = Modifier
                                .padding(horizontal = MaterialTheme.spacing.large)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {
                            AppLoadingIndicator()
                            Text(
                                text = stringResource(
                                    R.string.banner_edit_import_progress,
                                    uiState.importStage.orEmpty(),
                                ),
                                style = MaterialTheme.typography.labelLarge
                            )
                            val progress = uiState.importProgress
                            if (progress != null) {
                                LinearWavyProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .padding(top = MaterialTheme.spacing.medium)
                                        .fillMaxWidth()
                                )
                            }
                        }

                    }

                }
            }

            Spacer(
                modifier = Modifier.height(MaterialTheme.spacing.large)
            )

            AnimatedVisibility(visible = !uploading) {
                Surface(
                    tonalElevation = 4.dp,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = MaterialTheme.spacing.extraLarge),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                    ) {
                        BannerColorPicker(
                            selectedColor = color,
                            onColorSelected = { drawController.color.value = it }
                        )
                        Row(
                            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.large),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.PenSize1,
                                contentDescription = null,
                                modifier = Modifier.size(38.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val sliderState = rememberSliderState(
                                value = strokeWidth,
                                trackRange = 1f..BannerLimits.MAX_WIDTH.toFloat()
                            )
                            sliderState.value = strokeWidth
                            Slider(
                                state = sliderState,
                                onValueChange = { drawController.strokeWidth.value = it },
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.PenSize5,
                                contentDescription = null,
                                modifier = Modifier.size(38.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(top = MaterialTheme.spacing.small)
                    .fillMaxWidth()
            ) {

                SplitButtonLayout(
                    leadingButton = {
                        SplitButtonDefaults.LeadingButton(
                            enabled = !busy,
                            onClick = {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }) {
                            Icon(
                                Icons.AttachFile,
                                modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                                contentDescription = "Localized description",
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                text = stringResource(R.string.banner_edit_import_image),
                            )
                        }
                    },
                    trailingButton = {
                        var modeMenuExpanded by remember { mutableStateOf(false) }
                        Box {
                            SplitButtonDefaults.TrailingButton(
                                enabled = !busy,
                                checked = modeMenuExpanded,
                                onCheckedChange = { modeMenuExpanded = it },
                            ) {
                                Icon(
                                    imageVector = Icons.SettingsFilled,
                                    contentDescription = stringResource(R.string.banner_edit_import_mode),
                                    modifier = Modifier.size(SplitButtonDefaults.TrailingIconSize),
                                )
                            }
                            DropdownMenu(
                                expanded = modeMenuExpanded,
                                shape = MaterialTheme.shapes.largeIncreased,
                                onDismissRequest = { modeMenuExpanded = false }
                            ) {
                                ImportMode.entries.forEach { mode ->
                                    DropdownMenuItem(
                                        text = { Text(stringResource(mode.labelRes)) },
                                        onClick = {
                                            onEvent(Event.OnSelectImportMode(mode))
                                            modeMenuExpanded = false
                                        },
                                        trailingIcon = {
                                            if (mode == uiState.importMode) {
                                                Icon(
                                                    imageVector = Icons.Check,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                )

                Button(
                    onClick = {
                        onEvent(Event.OnSaveDrawing(drawController.strokes.toList()))
                    },
                    enabled = !busy && canUndo,
                ) {
                    Text(stringResource(R.string.banner_edit_save))
                }
            }

            FilledTonalButton(
                onClick = { drawController.clear() },
                enabled = !busy,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(stringResource(R.string.banner_edit_clear_canvas))
            }
        }
    }
}

@Composable
fun BannerColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color.Black, Color.White, Color.Red, Color.Blue,
        Color.Green, Color.Yellow, Color.Cyan, Color.Magenta,
        Color(0xFFFF5722), Color(0xFF795548), Color(0xFF9C27B0)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        items(colors) { color ->
            ColorItem(
                color = color,
                onClick = { onColorSelected(color) },
                isSelected = selectedColor == color
            )
        }
    }
}

@Preview
@Composable
fun BannerEditScreenPreview() {
    AppTheme(
        content = {
            BannerEditScreen(UiState(uploadStatus = UploadStatus.UPLOADING)) {}
        },
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun BannerEditScreenImportingPreview() {
    AppTheme(
        content = {
            BannerEditScreen(
                UiState(importStage = "Мазки", importProgress = 0.4f),
            ) {}
        },
    )
}
