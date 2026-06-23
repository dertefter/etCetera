package com.dertefter.design.components.avatar

import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.palette.graphics.Palette
import com.dertefter.design.components.common.RoundedPolygonShape
import com.dertefter.design.theme.AppTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.ktx.harmonize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SmallEmojiAvatar(
    modifier: Modifier = Modifier,
    emoji: String,
    containerSize: Dp = 48.dp,
    staticShape:  RoundedPolygon? = null,
    fontSize: TextUnit = 20.sp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    harmonizeColor: Color = MaterialTheme.colorScheme.primaryContainer,
    onClick: () -> Unit = {}
) {

    var detectedColor by remember { mutableStateOf(containerColor) }

    val polygon = remember(emoji, staticShape) {
        staticShape ?: getEmojiPolygon(emoji)
    }
    val clip = remember(polygon) {
        RoundedPolygonShape(polygon = polygon)
    }

    LaunchedEffect(emoji, containerColor) {
        val color = extractEmojiColor(emoji, containerColor.toArgb())
        detectedColor = color
            .harmonize(harmonizeColor, true)
    }

    Box(
        modifier = modifier
            .size(containerSize)
            .clip(clip)
            .background(containerColor)
            .background(detectedColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            maxLines = 1,
            fontSize = fontSize,
            modifier = Modifier,
            textAlign = TextAlign.Center
        )
    }
}

private fun getEmojiPolygon(emoji: String): RoundedPolygon {
    val random = Random(emoji.hashCode().toLong())
    val numVertices = (random.nextInt(4) + 2) * 2 // 4, 6, 8, 10
    val innerRadius = 0.2f + random.nextFloat() * 0.4f // 0.2f to 0.6f
    val rounding = 0.1f + random.nextFloat() * 0.6f // 0.1f to 0.7f
    return RoundedPolygon.star(
        numVerticesPerRadius = numVertices,
        innerRadius = innerRadius,
        rounding = CornerRounding(rounding)
    )
}

private suspend fun extractEmojiColor(
    emoji: String,
    defaultColorInt: Int
): Color = withContext(Dispatchers.Default) {
    val colorInt = runCatching {
        val size = 16
        val bitmap = createBitmap(size, size)
        val canvas = Canvas(bitmap)

        val paint = Paint().apply {
            textSize = size * 1f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        val x = size / 2f
        val y = (size / 2f) - ((paint.descent() + paint.ascent()) / 2f)
        canvas.drawText(emoji, x, y, paint)
        val palette = Palette.from(bitmap).generate()
        palette.getVibrantColor(
            palette.getDominantColor(
                palette.getMutedColor(
                    palette.getDarkVibrantColor( defaultColorInt )
                )
            )
        )
    }.getOrDefault(defaultColorInt)
    Color(colorInt)
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun PreviewAv() {
    AppTheme(
        paletteStyle = PaletteStyle.Vibrant,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        seedColor = Color.Green.toArgb().toLong()
    ) {
        val emojiList = listOf(
            "🌏", "🪲", "⚙️", "😍", "🖼️", "❤️", "😆", "🔥", "🌈", "🍎", "⚽", "🚗", "📱", "💻", "⌚",
            "🎧", "📷", "💡", "🔑", "🎁", "🎈", "🎉", "🎨", "🎭", "🎮", "🎲", "🎯", "🎳"
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(2.dp)
        ) {
            items(emojiList) { emoji ->
                SmallEmojiAvatar(emoji = emoji)
            }
        }
    }
}
