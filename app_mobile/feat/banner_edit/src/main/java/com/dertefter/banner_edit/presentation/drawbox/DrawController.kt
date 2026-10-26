package com.dertefter.banner_edit.presentation.drawbox

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.flow.MutableStateFlow

class DrawController {
    val color = MutableStateFlow(Color.Black)
    val strokeWidth = MutableStateFlow(10f)
    val enabled = MutableStateFlow(true)

    private var currentBitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).apply {
        eraseColor(android.graphics.Color.WHITE)
    }
    private var bitmapCanvas: Canvas = Canvas(currentBitmap)

    private var _imageBitmapState by mutableStateOf(currentBitmap.asImageBitmap())

    val internalBitmap: ImageBitmap
        get() = _imageBitmapState

    internal var drawVersion by mutableIntStateOf(0)
        private set

    fun open(imageBitmap: ImageBitmap) {
        val src = imageBitmap.asAndroidBitmap()
        val w = if (src.width > 0) src.width else 1
        val h = if (src.height > 0) src.height else 1
        val mutableBitmap = try {
            src.copy(Bitmap.Config.ARGB_8888, true)
        } catch (_: Exception) {
            Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).also { newBm ->
                Canvas(newBm).drawBitmap(src, 0f, 0f, null)
            }
        }
        currentBitmap = mutableBitmap
        bitmapCanvas = Canvas(currentBitmap)
        _imageBitmapState = currentBitmap.asImageBitmap()
        drawVersion++
    }

    internal fun initSizeIfNeeded(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        if (currentBitmap.width == 1 && currentBitmap.height == 1) {
            val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            newBitmap.eraseColor(android.graphics.Color.WHITE)
            currentBitmap = newBitmap
            bitmapCanvas = Canvas(currentBitmap)
            _imageBitmapState = currentBitmap.asImageBitmap()
            drawVersion++
        }
    }

    internal fun startStroke(
        x: Float,
        y: Float,
        canvasWidth: Float,
        canvasHeight: Float
    ) {
        if (currentBitmap.width <= 0 || currentBitmap.height <= 0 || canvasWidth <= 0 || canvasHeight <= 0) return
        val scaleX = currentBitmap.width.toFloat() / canvasWidth
        val scaleY = currentBitmap.height.toFloat() / canvasHeight
        val scaleStroke = (scaleX + scaleY) / 2f

        val paint = Paint().apply {
            color = this@DrawController.color.value.toArgb()
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val radius = (this@DrawController.strokeWidth.value * scaleStroke) / 2f
        bitmapCanvas.drawCircle(x * scaleX, y * scaleY, radius, paint)
        drawVersion++
    }

    internal fun drawQuadSegment(
        startX: Float, startY: Float,
        controlX: Float, controlY: Float,
        endX: Float, endY: Float,
        canvasWidth: Float, canvasHeight: Float
    ) {
        if (currentBitmap.width <= 0 || currentBitmap.height <= 0 || canvasWidth <= 0 || canvasHeight <= 0) return
        val scaleX = currentBitmap.width.toFloat() / canvasWidth
        val scaleY = currentBitmap.height.toFloat() / canvasHeight
        val scaleStroke = (scaleX + scaleY) / 2f

        val paint = Paint().apply {
            color = this@DrawController.color.value.toArgb()
            strokeWidth = this@DrawController.strokeWidth.value * scaleStroke
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        val path = Path().apply {
            moveTo(startX * scaleX, startY * scaleY)
            quadTo(controlX * scaleX, controlY * scaleY, endX * scaleX, endY * scaleY)
        }
        bitmapCanvas.drawPath(path, paint)
        drawVersion++
    }

    internal fun endStroke(
        lastMidX: Float, lastMidY: Float,
        finalX: Float, finalY: Float,
        hasMid: Boolean,
        canvasWidth: Float, canvasHeight: Float
    ) {
        if (!hasMid) return
        if (currentBitmap.width <= 0 || currentBitmap.height <= 0 || canvasWidth <= 0 || canvasHeight <= 0) return
        val scaleX = currentBitmap.width.toFloat() / canvasWidth
        val scaleY = currentBitmap.height.toFloat() / canvasHeight
        val scaleStroke = (scaleX + scaleY) / 2f

        val paint = Paint().apply {
            color = this@DrawController.color.value.toArgb()
            strokeWidth = this@DrawController.strokeWidth.value * scaleStroke
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        val path = Path().apply {
            moveTo(lastMidX * scaleX, lastMidY * scaleY)
            lineTo(finalX * scaleX, finalY * scaleY)
        }
        bitmapCanvas.drawPath(path, paint)
        drawVersion++
    }
}
