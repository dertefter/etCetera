package com.dertefter.banner_edit.presentation

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import coil3.BitmapImage
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import com.dertefter.banner_edit.R
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.components.loading.Shimmer
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import uk.codecymru.drawbox.box.DrawBox
import uk.codecymru.drawbox.controller.DrawController
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.roundToInt

private fun triggerHapticTick(haptic: HapticFeedback) {
    try {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    } catch (_: Exception) {}
}

@Composable
fun BannerEditScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val context = LocalContext.current
    val imageLoader = remember { ImageLoader(context) }
    val drawController = remember { DrawController() }
    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    val haptic = LocalHapticFeedback.current

    val color by drawController.color.collectAsState()
    val strokeWidth by drawController.strokeWidth.collectAsState()

    val historyStack = remember { mutableStateListOf<Bitmap>() }
    val redoStack = remember { mutableStateListOf<Bitmap>() }
    var wasDrawing by remember { mutableStateOf(false) }

    val canUndoAction = historyStack.size > 1
    val canRedoAction = redoStack.isNotEmpty()

    var pendingImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(1f) }
    var rawRotation by remember { mutableFloatStateOf(0f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var lastSnappedAngle by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(Unit) {
        drawController.enabled.value = true
    }

    LaunchedEffect(containerSize) {
        if (containerSize.width > 0 && containerSize.height > 0 && historyStack.isEmpty()) {
            val bm = drawController.internalBitmap.asAndroidBitmap()
            if (bm.width > 1 && bm.height > 1) {
                historyStack.add(bm.copy(bm.config ?: Bitmap.Config.ARGB_8888, true))
            }
        }
    }

    LaunchedEffect(uiState.uri) {
        uiState.uri?.let { uri ->
            val request = ImageRequest.Builder(context)
                .data(uri)
                .allowHardware(false)
                .build()
            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                val originalBitmap = (result.image as BitmapImage).bitmap
                val targetRatio = 16f / 9f
                val width = originalBitmap.width
                val height = originalBitmap.height
                val currentRatio = width.toFloat() / height

                val croppedBitmap = if (currentRatio > targetRatio) {
                    val targetWidth = (height * targetRatio).toInt()
                    val offset = (width - targetWidth) / 2
                    Bitmap.createBitmap(originalBitmap, offset, 0, targetWidth, height)
                } else {
                    val targetHeight = (width / targetRatio).toInt()
                    val offset = (height - targetHeight) / 2
                    Bitmap.createBitmap(originalBitmap, 0, offset, width, targetHeight)
                }
                drawController.open(croppedBitmap.asImageBitmap())
                historyStack.clear()
                redoStack.clear()
                historyStack.add(croppedBitmap.copy(croppedBitmap.config ?: Bitmap.Config.ARGB_8888, true))
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                scope.launch {
                    val request = ImageRequest.Builder(context)
                        .data(uri)
                        .allowHardware(false)
                        .build()
                    val result = imageLoader.execute(request)
                    if (result is SuccessResult) {
                        val originalBitmap = (result.image as BitmapImage).bitmap
                        pendingImageBitmap = originalBitmap
                        scale = 1f
                        rawRotation = 0f
                        rotation = 0f
                        lastSnappedAngle = 0f

                        if (containerSize.width > 0 && containerSize.height > 0) {
                            val imageAspectRatio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
                            val maxW = containerSize.width * 0.5f
                            val maxH = containerSize.height * 0.5f
                            val initScale = minOf(
                                maxW / originalBitmap.width,
                                maxH / originalBitmap.height,
                                1f
                            )
                            val initW = originalBitmap.width * initScale
                            val initH = initW / imageAspectRatio
                            offsetX = (containerSize.width - initW) / 2f
                            offsetY = (containerSize.height - initH) / 2f
                        } else {
                            offsetX = 0f
                            offsetY = 0f
                        }
                        drawController.enabled.value = false
                    }
                }
            }
        }
    )

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
                    IconButton(
                        onClick = {
                            if (historyStack.size > 1) {
                                val undone = historyStack.removeAt(historyStack.lastIndex)
                                redoStack.add(undone)
                                val previous = historyStack.last()
                                drawController.open(previous.asImageBitmap())
                            }
                        },
                        enabled = canUndoAction && pendingImageBitmap == null
                    ) {
                        Icon(
                            imageVector = Icons.Undo,
                            contentDescription = stringResource(R.string.banner_edit_undo),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            if (redoStack.isNotEmpty()) {
                                val redone = redoStack.removeAt(redoStack.lastIndex)
                                historyStack.add(redone)
                                drawController.open(redone.asImageBitmap())
                            }
                        },
                        enabled = canRedoAction && pendingImageBitmap == null
                    ) {
                        Icon(
                            imageVector = Icons.Redo,
                            contentDescription = stringResource(R.string.banner_edit_redo),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    FilledTonalIconButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = MaterialTheme.shapes.medium,
                        enabled = uiState.uploadStatus != UploadStatus.UPLOADING && pendingImageBitmap == null
                    ) {
                        Icon(
                            imageVector = Icons.AttachFile,
                            contentDescription = stringResource(R.string.banner_edit_select_file)
                        )
                    }
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
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                    .onSizeChanged { containerSize = it },
                contentAlignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    if (event.changes.all { !it.pressed }) {
                                        if (wasDrawing) {
                                            wasDrawing = false
                                            val bm = drawController.internalBitmap.asAndroidBitmap()
                                            if (bm.width > 1 && bm.height > 1) {
                                                val snapshot = bm.copy(bm.config ?: Bitmap.Config.ARGB_8888, true)
                                                historyStack.add(snapshot)
                                                if (historyStack.size > 25) {
                                                    historyStack.removeAt(0)
                                                }
                                                redoStack.clear()
                                            }
                                        }
                                    } else if (event.changes.any { it.pressed }) {
                                        if (drawController.enabled.value && pendingImageBitmap == null) {
                                            wasDrawing = true
                                        }
                                    }
                                }
                            }
                        }
                ) {
                    DrawBox(
                        controller = drawController,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (pendingImageBitmap != null) {
                    val currentBitmap = pendingImageBitmap!!
                    val imageAspectRatio = currentBitmap.width.toFloat() / currentBitmap.height.toFloat()

                    val maxW = containerSize.width * 0.5f
                    val maxH = containerSize.height * 0.5f
                    val baseScale = minOf(
                        maxW / currentBitmap.width,
                        maxH / currentBitmap.height,
                        1f
                    )
                    val baseW = currentBitmap.width * baseScale

                    val currentW = baseW * scale
                    val currentH = currentW / imageAspectRatio

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                            .width(with(LocalDensity.current) { currentW.toDp() })
                            .aspectRatio(imageAspectRatio)
                            .graphicsLayer {
                                rotationZ = rotation
                            }
                            .pointerInput(currentBitmap) {
                                detectTransformGestures { _, pan, zoom, rot ->
                                    scale = (scale * zoom).coerceIn(0.1f, 15.0f)
                                    rawRotation += rot
                                    offsetX += pan.x
                                    offsetY += pan.y

                                    val snapThreshold = 5f
                                    val nearestSnap = (round(rawRotation / 90f) * 90f)
                                    val diff = abs(rawRotation - nearestSnap)

                                    if (diff <= snapThreshold) {
                                        if (lastSnappedAngle != nearestSnap) {
                                            triggerHapticTick(haptic)
                                            lastSnappedAngle = nearestSnap
                                        }
                                        rotation = nearestSnap
                                    } else {
                                        lastSnappedAngle = null
                                        rotation = rawRotation
                                    }
                                }
                            }
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            bitmap = currentBitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopEnd),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
                        tonalElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(all = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    pendingImageBitmap = null
                                    drawController.enabled.value = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Close,
                                    contentDescription = stringResource(R.string.banner_edit_cancel_placement),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }

                            FilledTonalIconButton(
                                onClick = {
                                    val bitmapToBake = pendingImageBitmap
                                    if (bitmapToBake != null && containerSize.width > 0 && containerSize.height > 0) {
                                        val bakeAspectRatio = bitmapToBake.width.toFloat() / bitmapToBake.height.toFloat()
                                        val scaleX = drawController.internalBitmap.width.toFloat() / containerSize.width.toFloat()
                                        val scaleY = drawController.internalBitmap.height.toFloat() / containerSize.height.toFloat()

                                        val targetW = currentW * scaleX
                                        val targetH = targetW / bakeAspectRatio

                                        val canvasCenterX = (offsetX + currentW / 2f) * scaleX
                                        val canvasCenterY = (offsetY + currentH / 2f) * scaleY

                                        val currentCanvasBitmap = drawController.internalBitmap.asAndroidBitmap()
                                        val combinedBitmap = createBitmap(
                                            currentCanvasBitmap.width,
                                            currentCanvasBitmap.height
                                        )
                                        val canvas = android.graphics.Canvas(combinedBitmap)
                                        canvas.drawBitmap(currentCanvasBitmap, 0f, 0f, null)

                                        val matrix = android.graphics.Matrix().apply {
                                            postScale(
                                                targetW / bitmapToBake.width.toFloat(),
                                                targetH / bitmapToBake.height.toFloat()
                                            )
                                            postTranslate(-targetW / 2f, -targetH / 2f)
                                            postRotate(rotation)
                                            postTranslate(canvasCenterX, canvasCenterY)
                                        }

                                        val paint = android.graphics.Paint().apply {
                                            isAntiAlias = true
                                            isFilterBitmap = true
                                        }

                                        canvas.drawBitmap(bitmapToBake, matrix, paint)

                                        drawController.open(combinedBitmap.asImageBitmap())

                                        val snapshot = combinedBitmap.copy(combinedBitmap.config ?: Bitmap.Config.ARGB_8888, true)
                                        historyStack.add(snapshot)
                                        if (historyStack.size > 25) {
                                            historyStack.removeAt(0)
                                        }
                                        redoStack.clear()
                                    }
                                    pendingImageBitmap = null
                                    drawController.enabled.value = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Check,
                                    contentDescription = stringResource(R.string.banner_edit_confirm_placement)
                                )
                            }
                        }
                    }
                }

                this@Column.AnimatedVisibility(
                    modifier = Modifier.fillMaxSize(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                    visible = uiState.uploadStatus == UploadStatus.UPLOADING
                ) {
                    Shimmer(
                        modifier = Modifier
                            .clickable(onClick = {})
                            .fillMaxSize()
                    )
                    AppLoadingIndicator(
                        modifier = Modifier.padding(60.dp)
                    )
                }

            }

            AnimatedVisibility(
                visible = uiState.uploadStatus != UploadStatus.UPLOADING && pendingImageBitmap == null
            ) {
                Surface(
                    tonalElevation = 4.dp,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth()
                )
                {
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
                                trackRange = 1f..100f
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
                modifier = Modifier.fillMaxWidth()
            ){

                FilledTonalButton(
                    onClick = {
                        val currentBm = drawController.internalBitmap.asAndroidBitmap()
                        val width = if (currentBm.width > 1) currentBm.width else 1000
                        val height = if (currentBm.height > 1) currentBm.height else 562

                        val blankBitmap = createBitmap(width, height).apply {
                            eraseColor(android.graphics.Color.WHITE)
                        }
                        drawController.open(blankBitmap.asImageBitmap())

                        val snapshot = blankBitmap.copy(blankBitmap.config ?: Bitmap.Config.ARGB_8888, true)
                        historyStack.add(snapshot)
                        if (historyStack.size > 25) {
                            historyStack.removeAt(0)
                        }
                        redoStack.clear()
                    },
                    enabled = uiState.uploadStatus != UploadStatus.UPLOADING && pendingImageBitmap == null
                ) {
                    Text(stringResource(R.string.banner_edit_clear_canvas))
                }

                Button(
                    onClick = {
                        onEvent(Event.OnSaveDrawing(drawController.internalBitmap.asAndroidBitmap()))
                    },
                    enabled = uiState.uploadStatus != UploadStatus.UPLOADING && pendingImageBitmap == null
                ) {
                    Text(stringResource(R.string.banner_edit_save))
                }
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
