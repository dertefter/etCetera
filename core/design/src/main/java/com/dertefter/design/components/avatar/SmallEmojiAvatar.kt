package com.dertefter.design.components.avatar

import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.graphics.shapes.RoundedPolygon
import androidx.palette.graphics.Palette
import com.dertefter.design.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SmallEmojiAvatar(
    modifier: Modifier = Modifier,
    emoji: String,
    containerSize: Dp = 48.dp,
    staticShape:  RoundedPolygon? = null,
    fontSize: TextUnit = 20.sp,
    angle: Int = 0,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer
) {

    var detectedColor by remember { mutableStateOf(containerColor) }

    LaunchedEffect(emoji, containerColor) {
        val color = extractEmojiColor(emoji, containerColor.toArgb())
        detectedColor = color.copy(alpha = 0.22f)
    }


    Box(
        modifier = modifier
            .clip(staticShape?.toShape(angle) ?: shapeForEmoji(emoji, angle))
            .background(detectedColor)
            .size(containerSize),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            maxLines = 1,
            fontSize = fontSize
        )
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private val avatarShapes = listOf(
    MaterialShapes.Circle,
    MaterialShapes.Square,
    MaterialShapes.Slanted,
    MaterialShapes.Arch,
    MaterialShapes.Arrow,
    MaterialShapes.Pill,
    MaterialShapes.Sunny,
    MaterialShapes.VerySunny,
    MaterialShapes.Cookie7Sided,
    MaterialShapes.Cookie9Sided,
    MaterialShapes.Cookie12Sided,
    MaterialShapes.Cookie4Sided,
    MaterialShapes.Cookie6Sided,
    MaterialShapes.Ghostish,
    MaterialShapes.Clover4Leaf,
    MaterialShapes.Clover8Leaf,
    MaterialShapes.SoftBurst,
    MaterialShapes.SoftBoom,
    MaterialShapes.Flower,
    MaterialShapes.PuffyDiamond,
    MaterialShapes.Bun,
)

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun shapeForEmoji(
    emoji: String,
    angle: Int
): Shape {
    val index = abs(emoji.hashCode()) % avatarShapes.size
    return avatarShapes[index].toShape(angle)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
//@Preview(showBackground = false)
@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SmallEmojiAvatarPreview() {
    AppTheme {
        val emojiList = listOf(
            "🐶", "💿", "🖼️",
        )

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            emojiList.chunked(6).forEach { rowEmojis ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowEmojis.forEach { emoji ->
                        SmallEmojiAvatar(emoji = emoji)
                    }
                }
            }
        }
    }
}


private suspend fun extractEmojiColor(
    emoji: String,
    defaultColorInt: Int
): Color = withContext(Dispatchers.Default) {
    val colorInt = runCatching {
        val size = 32
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