package com.dertefter.banner_edit.presentation.drawbox

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import com.dertefter.banner_edit.domain.BannerLimits

@Composable
fun DrawBox(
    controller: DrawController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .pointerInput(controller) {
                handleDrawGestures(controller)
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color.White)
            val sx = size.width / BannerLimits.W
            val sy = size.height / BannerLimits.H
            val all = controller.strokes + listOfNotNull(controller.currentStroke)
            for (s in all) drawBannerStroke(s, sx, sy)
        }
    }
}

private fun DrawScope.drawBannerStroke(stroke: BannerStroke, sx: Float, sy: Float) {
    val color = parseHexColor(stroke.colorHex)
    val widthPx = stroke.width * sx
    val points = stroke.points.map { Offset(it.x * sx, it.y * sy) }
    if (points.isEmpty()) return

    if (points.size == 1) {
        drawCircle(color = color, radius = widthPx / 2f, center = points[0])
    } else {
        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = widthPx, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

private fun parseHexColor(hex: String): Color {
    val rgb = hex.removePrefix("#").toLong(16)
    return Color(0xFF000000L or rgb)
}

private suspend fun PointerInputScope.handleDrawGestures(controller: DrawController) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        if (!controller.enabled.value) return@awaitEachGesture

        val canvasWidth = size.width.toFloat()
        val canvasHeight = size.height.toFloat()
        val pointerId = down.id

        controller.startStroke(down.position.x, down.position.y, canvasWidth, canvasHeight)

        while (true) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull { it.id == pointerId }
            if (change == null || !change.pressed) {
                controller.endStroke()
                break
            }
            change.consume()
            controller.extendStroke(change.position.x, change.position.y, canvasWidth, canvasHeight)
        }
    }
}
