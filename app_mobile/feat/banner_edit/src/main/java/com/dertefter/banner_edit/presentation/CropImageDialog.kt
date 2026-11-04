package com.dertefter.banner_edit.presentation

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.createBitmap
import com.dertefter.banner_edit.R
import com.dertefter.banner_edit.domain.BannerLimits
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

@Composable
fun CropImageDialog(
    uri: Uri,
    application: Application,
    onConfirm: (CropRect) -> Unit,
    onCancel: () -> Unit,
) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var srcWidth by remember { mutableIntStateOf(0) }
    var srcHeight by remember { mutableIntStateOf(0) }
    var loadError by remember { mutableStateOf(false) }

    val isPreview = LocalInspectionMode.current
    LaunchedEffect(uri, isPreview) {
        if (isPreview) {
            val bmp = createBitmap(1100, 380)
            srcWidth = bmp.width
            srcHeight = bmp.height
            imageBitmap = bmp.asImageBitmap()
            return@LaunchedEffect
        }
        val bmp = withContext(Dispatchers.IO) {
            decodeDownsampled(application, uri)
        }
        if (bmp != null) {
            srcWidth = bmp.width
            srcHeight = bmp.height
            imageBitmap = bmp.asImageBitmap()
        } else {
            loadError = true
        }
    }

    LaunchedEffect(loadError) {
        if (loadError) onCancel()
    }

    if (isPreview) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black,
        ) {
            CropImageContent(
                imageBitmap = createBitmap(1100, 380).asImageBitmap(),
                srcWidth = 1100,
                srcHeight = 380,
                onConfirm = onConfirm,
                onCancel = onCancel,
            )
        }
    } else {
        Dialog(
            onDismissRequest = onCancel,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black,
            ) {
                val img = imageBitmap
                if (img == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    CropImageContent(
                        imageBitmap = img,
                        srcWidth = srcWidth,
                        srcHeight = srcHeight,
                        onConfirm = onConfirm,
                        onCancel = onCancel,
                    )
                }
            }
        }
    }
}

@Composable
private fun CropImageContent(
    imageBitmap: ImageBitmap,
    srcWidth: Int,
    srcHeight: Int,
    onConfirm: (CropRect) -> Unit,
    onCancel: () -> Unit,
) {
    val aspectRatio = BannerLimits.W.toFloat() / BannerLimits.H.toFloat()

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var zoom by remember { mutableFloatStateOf(1f) }

    val frameWidthPx: Float
    val frameHeightPx: Float

    if (canvasSize.width > 0 && canvasSize.height > 0) {
        val cw = canvasSize.width.toFloat()
        val ch = canvasSize.height.toFloat()
        val usableH = ch * 0.75f
        val frameByWidth = cw * 0.95f
        val frameByHeight = frameByWidth / aspectRatio
        if (frameByHeight <= usableH) {
            frameWidthPx = frameByWidth
            frameHeightPx = frameByHeight
        } else {
            frameHeightPx = usableH
            frameWidthPx = usableH * aspectRatio
        }
    } else {
        frameWidthPx = 0f
        frameHeightPx = 0f
    }

    val baseScale = if (srcWidth > 0 && srcHeight > 0 && frameWidthPx > 0f) {
        max(frameWidthPx / srcWidth, frameHeightPx / srcHeight)
    } else 1f

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { canvasSize = it }
                .pointerInput(canvasSize) {
                    if (canvasSize.width == 0) return@pointerInput
                    detectTransformGestures { _, pan, zoomChange, _ ->
                        val newZoom = (zoom * zoomChange).coerceIn(1f, 5f)
                        zoom = newZoom
                        val effScale = baseScale * newZoom
                        val dispW = srcWidth * effScale
                        val dispH = srcHeight * effScale
                        val clampX = max(0f, (dispW - frameWidthPx) / 2f)
                        val clampY = max(0f, (dispH - frameHeightPx) / 2f)
                        val newPan = panOffset + pan
                        panOffset = Offset(
                            newPan.x.coerceIn(-clampX, clampX),
                            newPan.y.coerceIn(-clampY, clampY),
                        )
                    }
                },
        ) {
            if (frameWidthPx <= 0f || srcWidth == 0) return@Canvas

            val cx = size.width / 2f
            val cy = size.height * 0.4f

            val effScale = baseScale * zoom
            val dispW = srcWidth * effScale
            val dispH = srcHeight * effScale

            drawImage(
                image = imageBitmap,
                dstOffset = IntOffset(
                    (cx - dispW / 2f + panOffset.x).toInt(),
                    (cy - dispH / 2f + panOffset.y).toInt(),
                ),
                dstSize = IntSize(dispW.toInt().coerceAtLeast(1), dispH.toInt().coerceAtLeast(1)),
            )

            val frameLeft = cx - frameWidthPx / 2f
            val frameTop = cy - frameHeightPx / 2f

            val scrim = Color.Black.copy(alpha = 0.6f)
            drawRect(scrim, Offset.Zero, Size(size.width, frameTop))
            drawRect(scrim, Offset(0f, frameTop + frameHeightPx), Size(size.width, size.height - frameTop - frameHeightPx))
            drawRect(scrim, Offset(0f, frameTop), Size(frameLeft, frameHeightPx))
            drawRect(scrim, Offset(frameLeft + frameWidthPx, frameTop), Size(size.width - frameLeft - frameWidthPx, frameHeightPx))

            drawRect(
                color = Color.White,
                topLeft = Offset(frameLeft, frameTop),
                size = Size(frameWidthPx, frameHeightPx),
                style = Stroke(width = 2.dp.toPx()),
            )

            val guideColor = Color.White.copy(alpha = 0.3f)
            val strokeW = 1.dp.toPx()
            for (i in 1..3) {
                val x = frameLeft + frameWidthPx * i / 3f
                drawLine(guideColor, Offset(x, frameTop), Offset(x, frameTop + frameHeightPx), strokeW)
            }
            val y = frameTop + frameHeightPx / 2f
            drawLine(guideColor, Offset(frameLeft, y), Offset(frameLeft + frameWidthPx, y), strokeW)
        }

        Row(
            modifier = Modifier
                .safeDrawingPadding()
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(all = MaterialTheme.spacing.defaultScreenPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.banner_edit_crop_cancel))
            }
            TextButton(onClick = {
                val cropRect = computeCropRect(
                    frameWidthPx = frameWidthPx,
                    frameHeightPx = frameHeightPx,
                    panOffset = panOffset,
                    zoom = zoom,
                    baseScale = baseScale,
                    srcWidth = srcWidth,
                    srcHeight = srcHeight,
                )
                onConfirm(cropRect)
            }) {
                Text(stringResource(R.string.banner_edit_crop_confirm))
            }
        }

        Text(
            text = stringResource(R.string.banner_edit_crop_hint),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier
                .safeDrawingPadding()
                .padding(all = MaterialTheme.spacing.medium)
                .align(Alignment.TopCenter),
        )
    }
}
private fun computeCropRect(
    frameWidthPx: Float,
    frameHeightPx: Float,
    panOffset: Offset,
    zoom: Float,
    baseScale: Float,
    srcWidth: Int,
    srcHeight: Int,
): CropRect {
    if (frameWidthPx <= 0f || baseScale <= 0f) {
        return CropRect(0, 0, srcWidth, srcHeight)
    }

    val effScale = baseScale * zoom

    val relLeft = -frameWidthPx / 2f - panOffset.x
    val relTop = -frameHeightPx / 2f - panOffset.y

    val srcLeft = relLeft / effScale + srcWidth / 2f
    val srcTop = relTop / effScale + srcHeight / 2f
    val srcRight = srcLeft + frameWidthPx / effScale
    val srcBottom = srcTop + frameHeightPx / effScale

    val l = srcLeft.toInt().coerceIn(0, srcWidth - 1)
    val t = srcTop.toInt().coerceIn(0, srcHeight - 1)
    val r = srcRight.toInt().coerceIn(l + 1, srcWidth)
    val b = srcBottom.toInt().coerceIn(t + 1, srcHeight)

    return CropRect(l, t, r, b)
}

private fun decodeDownsampled(application: Application, uri: Uri): Bitmap? {
    return runCatching {
        val resolver = application.contentResolver
        val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, boundsOpts) }
        if (boundsOpts.outWidth <= 0 || boundsOpts.outHeight <= 0) return null

        var sample = 1
        val maxDim = max(boundsOpts.outWidth, boundsOpts.outHeight)
        while (maxDim / sample > 4096) sample *= 2

        val opts = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
    }.getOrNull()
}

@Preview(showSystemUi = true)
@Composable
fun CropImageDialogPreview() {
    val context = LocalContext.current
    val application = (context.applicationContext as? Application) ?: Application()
    AppTheme {
        CropImageDialog(
            uri = Uri.EMPTY,
            application = application,
            onConfirm = {},
            onCancel = {},
        )
    }
}

