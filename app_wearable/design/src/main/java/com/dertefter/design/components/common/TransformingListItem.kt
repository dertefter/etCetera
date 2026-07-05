package com.dertefter.design.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.times
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastIsFinite
import androidx.compose.ui.util.fastRoundToInt
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnItemScope
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.lazy.TransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight

@Composable
fun TransformingLazyColumnItemScope.TransformingListItem(  modifier: Modifier = Modifier,
    transformationSpec: TransformationSpec,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .transformedHeight(this, transformationSpec)
            .surface(
                transformation = SurfaceTransformation(transformationSpec),
                painter = ColorPainter(Color.Transparent),
                shape = RoundedCornerShape(0.dp),
            )
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        content()
    }
}

@Composable
fun Modifier.surface(
    transformation: SurfaceTransformation?,
    painter: Painter,
    shape: Shape = RectangleShape,
    border: BorderStroke? = null,
): Modifier =
    if (transformation != null && transformation != NoOpSurfaceTransformation) {
        val backgroundPainter =
            remember(transformation, painter, shape, border) {
                transformation.createContainerPainter(painter, shape, border)
            }

        // We first apply the container transformation, then the Modifier `surface` is applied to,
        // then the painter and finally the content transformation.
        Modifier.graphicsLayer { with(transformation) { applyContainerTransformation() } }
            .then(this)
            .paintBackground(painter = backgroundPainter)
            .graphicsLayer {
                this.shape = shape
                with(transformation) { applyContentTransformation() }
                clip = true
            }
    } else {
        val borderModifier = if (border != null) border(border = border, shape = shape) else this
        borderModifier
            .clip(shape = shape)
            .paintBackground(painter = painter, contentScale = ContentScale.Crop)
    }
fun Modifier.paintBackground(
    painter: Painter,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Inside,
) = this then PainterElement(painter = painter, alignment = alignment, contentScale = contentScale)

private data class PainterElement(
    val painter: Painter,
    val contentScale: ContentScale,
    val alignment: Alignment = Alignment.Center,
) : ModifierNodeElement<PainterNode>() {
    override fun create(): PainterNode {
        return PainterNode(painter = painter, alignment = alignment, contentScale = contentScale)
    }

    override fun update(node: PainterNode) {
        node.painter = painter
        node.contentScale = contentScale
        node.alignment = alignment
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "paint"
        properties["painter"] = painter
        properties["alignment"] = alignment
        properties["contentScale"] = contentScale
    }
}

private class PainterNode(
    var painter: Painter,
    var alignment: Alignment = Alignment.Center,
    var contentScale: ContentScale,
) : Modifier.Node(), DrawModifierNode {

    override fun ContentDrawScope.draw() {
        val intrinsicSize = painter.intrinsicSize
        val srcWidth =
            if (intrinsicSize.hasSpecifiedAndFiniteWidth()) {
                intrinsicSize.width
            } else {
                size.width
            }

        val srcHeight =
            if (intrinsicSize.hasSpecifiedAndFiniteHeight()) {
                intrinsicSize.height
            } else {
                size.height
            }

        val srcSize = Size(srcWidth, srcHeight)
        val scaledSize =
            if (size.width != 0f && size.height != 0f) {
                srcSize * contentScale.computeScaleFactor(srcSize, size)
            } else {
                Size.Zero
            }

        val alignedPosition =
            alignment.align(
                IntSize(scaledSize.width.fastRoundToInt(), scaledSize.height.fastRoundToInt()),
                IntSize(size.width.fastRoundToInt(), size.height.fastRoundToInt()),
                layoutDirection,
            )

        val dx = alignedPosition.x.toFloat()
        val dy = alignedPosition.y.toFloat()

        translate(dx, dy) { with(painter) { draw(size = scaledSize) } }

        // Maintain the same pattern as Modifier.drawBehind to allow chaining of DrawModifiers
        drawContent()
    }

    private fun Size.hasSpecifiedAndFiniteWidth() = this != Size.Unspecified && width.fastIsFinite()

    private fun Size.hasSpecifiedAndFiniteHeight() =
        this != Size.Unspecified && height.fastIsFinite()

    override fun toString(): String =
        "PainterModifier(" +
                "painter=$painter, " +
                "alignment=$alignment, " +
                "contentScale=$contentScale)"
}


val NoOpSurfaceTransformation: SurfaceTransformation =
    object : SurfaceTransformation {
        override fun createContainerPainter(
            painter: Painter,
            shape: Shape,
            border: BorderStroke?,
        ): Painter =
            object : Painter() {
                override val intrinsicSize: Size
                    get() = Size.Unspecified

                private var lastUsedSize = Size.Unspecified
                private val cachedPath: Path = Path()

                override fun DrawScope.onDraw() {
                    if (size != lastUsedSize) {
                        cachedPath.reset()
                        cachedPath.addOutline(
                            shape.createOutline(size, layoutDirection, this@onDraw)
                        )
                        lastUsedSize = size
                    }

                    clipPath(path = cachedPath) {
                        if (border != null) {
                            drawOutline(
                                outline = shape.createOutline(size, layoutDirection, this@onDraw),
                                brush = border.brush,
                                style = Stroke(border.width.toPx()),
                            )
                        }
                        with(painter) { draw(size) }
                    }
                }
            }

        override fun GraphicsLayerScope.applyContentTransformation() {}

        override fun GraphicsLayerScope.applyContainerTransformation() {}
    }