package com.dertefter.banner_edit.presentation.drawbox

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.dertefter.banner_edit.domain.BannerLimits
import kotlinx.coroutines.flow.MutableStateFlow


class DrawController {
    val color = MutableStateFlow(Color.Black)
    val strokeWidth = MutableStateFlow(10f)
    val enabled = MutableStateFlow(true)

    val strokes: List<BannerStroke>
        field = mutableStateListOf<BannerStroke>()

    private val _redoStack = mutableStateListOf<BannerStroke>()
    val canUndo: Boolean get() = strokes.isNotEmpty()
    val canRedo: Boolean get() = _redoStack.isNotEmpty()

    var currentStroke by mutableStateOf<BannerStroke?>(null)
        private set

    private val currentPoints = mutableListOf<Offset>()

    fun startStroke(x: Float, y: Float, canvasWidth: Float, canvasHeight: Float) {
        if (canvasWidth <= 0f || canvasHeight <= 0f) return
        currentPoints.clear()
        currentPoints.add(toCanvas(x, y, canvasWidth, canvasHeight))
        publishCurrent()
    }

    fun extendStroke(x: Float, y: Float, canvasWidth: Float, canvasHeight: Float) {
        if (currentPoints.isEmpty() || canvasWidth <= 0f || canvasHeight <= 0f) return
        currentPoints.add(toCanvas(x, y, canvasWidth, canvasHeight))
        publishCurrent()
    }

    fun endStroke() {
        if (currentPoints.isNotEmpty()) {
            strokes.add(BannerStroke(color.value.toHexRrggbb(), strokeWidth.value, currentPoints.toList()))
            currentPoints.clear()
            _redoStack.clear()
        }
        currentStroke = null
    }

    fun undo() {
        if (strokes.isNotEmpty()) _redoStack.add(strokes.removeAt(strokes.lastIndex))
    }

    fun redo() {
        if (_redoStack.isNotEmpty()) strokes.add(_redoStack.removeAt(_redoStack.lastIndex))
    }

    fun clear() {
        strokes.clear()
        _redoStack.clear()
        currentPoints.clear()
        currentStroke = null
    }

    fun loadStrokes(loaded: List<BannerStroke>) {
        strokes.clear()
        _redoStack.clear()
        currentPoints.clear()
        currentStroke = null
        strokes.addAll(loaded)
    }

    private fun publishCurrent() {
        currentStroke = BannerStroke(color.value.toHexRrggbb(), strokeWidth.value, currentPoints.toList())
    }

    private fun toCanvas(x: Float, y: Float, canvasWidth: Float, canvasHeight: Float): Offset =
        Offset(x / canvasWidth * BannerLimits.W, y / canvasHeight * BannerLimits.H)
}
