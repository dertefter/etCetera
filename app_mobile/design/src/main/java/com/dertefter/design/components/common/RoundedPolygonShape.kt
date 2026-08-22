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
import kotlin.math.abs
import kotlin.math.max
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
        val maxAbsX = max(abs(bounds[0]), abs(bounds[2]))
        val maxAbsY = max(abs(bounds[1]), abs(bounds[3]))
        val scale = min(size.width / (2 * maxAbsX), size.height / (2 * maxAbsY))

        matrix.reset()
        matrix.translate(size.width / 2f, size.height / 2f)
        matrix.scale(scale, scale)

        transformedPath.rewind()
        transformedPath.addPath(basePath)
        transformedPath.transform(matrix)

        return Outline.Generic(transformedPath)
    }
}