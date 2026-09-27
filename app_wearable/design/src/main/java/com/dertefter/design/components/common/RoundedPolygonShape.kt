package com.dertefter.design.components.common

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import kotlin.math.min

class RoundedPolygonShape(
    private val polygon: RoundedPolygon
) : Shape {
    private val basePath = polygon.toPath().asComposePath()
    private val matrix = Matrix()
    private val transformedPath = Path()

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val bounds = polygon.calculateBounds()
        val boundsWidth = bounds[2] - bounds[0]
        val boundsHeight = bounds[3] - bounds[1]

        if (boundsWidth <= 0f || boundsHeight <= 0f) {
            return Outline.Generic(basePath)
        }

        val boundsCenterX = (bounds[0] + bounds[2]) / 2f
        val boundsCenterY = (bounds[1] + bounds[3]) / 2f

        val scale = min(size.width / boundsWidth, size.height / boundsHeight)

        matrix.reset()
        matrix.translate(size.width / 2f, size.height / 2f)
        matrix.scale(scale, scale)
        matrix.translate(-boundsCenterX / 2, boundsCenterY / 2)

        transformedPath.rewind()
        transformedPath.addPath(basePath)
        transformedPath.transform(matrix)

        return Outline.Generic(transformedPath)
    }
}