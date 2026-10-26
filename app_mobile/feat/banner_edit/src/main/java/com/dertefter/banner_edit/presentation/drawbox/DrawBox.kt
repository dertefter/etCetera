package com.dertefter.banner_edit.presentation.drawbox

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

@Composable
fun DrawBox(
    controller: DrawController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .onSizeChanged { size ->
                controller.initSizeIfNeeded(size.width, size.height)
            }
            .pointerInput(controller) {
                handleDrawGestures(controller)
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            controller.drawVersion

            val imageBitmap = controller.internalBitmap
            drawImage(
                image = imageBitmap,
                dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
            )
        }
    }
}

private suspend fun PointerInputScope.handleDrawGestures(controller: DrawController) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        if (!controller.enabled.value) return@awaitEachGesture

        var lastX = down.position.x
        var lastY = down.position.y
        var lastMidX = lastX
        var lastMidY = lastY
        var hasMid = false

        val canvasWidth = size.width.toFloat()
        val canvasHeight = size.height.toFloat()

        if (canvasWidth > 0 && canvasHeight > 0) {
            controller.startStroke(lastX, lastY, canvasWidth, canvasHeight)
        }

        val pointerId = down.id
        while (true) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull { it.id == pointerId } ?: break
            if (!change.pressed) {
                if (canvasWidth > 0 && canvasHeight > 0) {
                    controller.endStroke(lastMidX, lastMidY, lastX, lastY, hasMid, canvasWidth, canvasHeight)
                }
                break
            }

            change.consume()

            val currentX = change.position.x
            val currentY = change.position.y

            if (canvasWidth > 0 && canvasHeight > 0) {
                val midX = (lastX + currentX) / 2f
                val midY = (lastY + currentY) / 2f

                if (!hasMid) {
                    controller.drawQuadSegment(
                        startX = lastX, startY = lastY,
                        controlX = lastX, controlY = lastY,
                        endX = midX, endY = midY,
                        canvasWidth = canvasWidth, canvasHeight = canvasHeight
                    )
                    hasMid = true
                } else {
                    controller.drawQuadSegment(
                        startX = lastMidX, startY = lastMidY,
                        controlX = lastX, controlY = lastY,
                        endX = midX, endY = midY,
                        canvasWidth = canvasWidth, canvasHeight = canvasHeight
                    )
                }

                lastMidX = midX
                lastMidY = midY
                lastX = currentX
                lastY = currentY
            }
        }
    }
}
