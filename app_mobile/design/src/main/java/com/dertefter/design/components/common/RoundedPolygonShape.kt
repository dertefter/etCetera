package com.dertefter.design.components.common

import androidx.compose.ui.geometry.Rect
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

fun RoundedPolygon.getBounds() = calculateBounds().let { Rect(it[0], it[1], it[2], it[3]) }

class RoundedPolygonShape(
    private val polygon: RoundedPolygon
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = polygon.toPath().asComposePath()
        val matrix = Matrix()
        val bounds = polygon.getBounds()
        val scale = min(size.width / bounds.width, size.height / bounds.height)
        matrix.translate(size.width / 2f, size.height / 2f)
        matrix.scale(scale, scale)
        matrix.translate(-(bounds.left + bounds.right) / 2f, -(bounds.top + bounds.bottom) / 2f)

        path.transform(matrix)
        return Outline.Generic(path)
    }
}