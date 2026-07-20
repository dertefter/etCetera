package com.dertefter.design.components.avatar

import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
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
import com.dertefter.design.theme.spacing
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.ktx.harmonize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EmojiAvatar(
    modifier: Modifier = Modifier,
    emoji: String,
    containerSize: Dp = 52.dp,
    staticShape:  RoundedPolygon? = null,
    strokeColor: Color = Color.Transparent,
    strokeWidth: Dp = 2.dp,
    fontSize: TextUnit = 20.sp,
    rotation: Float = 0f,
    onClick: () -> Unit = {}
) {

    val targetBgColor = MaterialTheme.colorScheme.primaryContainer
    val targetShadowColor = MaterialTheme.colorScheme.onPrimaryFixed
    val fallbackColor = MaterialTheme.colorScheme.surfaceContainer

    var baseEmojiColor by remember(emoji) { mutableStateOf<Color?>(null) }

    LaunchedEffect(emoji) {
        baseEmojiColor = extractEmojiColor(
            emoji = emoji,
            defaultColorInt = Color.Transparent.toArgb()
        )
    }

    val harmonizedBgColor = remember(baseEmojiColor, targetBgColor) {
        val color = baseEmojiColor ?: return@remember fallbackColor
        if (color == Color.Transparent) fallbackColor
        else color.harmonize(targetBgColor, matchSaturation = true)
    }

    val harmonizedShadowColor = remember(baseEmojiColor, targetShadowColor) {
        val color = baseEmojiColor ?: return@remember fallbackColor
        if (color == Color.Transparent) fallbackColor
        else color.harmonize(targetShadowColor, matchSaturation = true)
    }

    val polygon = remember(emoji, staticShape) {
        staticShape ?: getEmojiPolygon(emoji)
    }

    val clip = remember(polygon, rotation) {
        RoundedPolygonShape(polygon = polygon, rotation = rotation)
    }

    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .graphicsLayer {
                this.shape = clip
                this.clip = true
            }
            .size(containerSize)
            .border(
                shape = clip,
                color = strokeColor,
                width = strokeWidth
            )
            .background(harmonizedBgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            maxLines = 1,
            fontSize = fontSize,
            modifier = Modifier,
            textAlign = TextAlign.Center,
            style = TextStyle(
                shadow = Shadow(
                    color = harmonizedShadowColor.copy(alpha = 0.6f),
                    blurRadius = 12f
                )
            )
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


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun PreviewAv() {
    var darkTheme by remember { mutableStateOf(true) }
    var rotation by remember { mutableStateOf(0f) }
    var currentEmojiIndex by remember { mutableIntStateOf(0) }

    AppTheme(
        seedColor = Color(0xFF5A1DC7).toArgb().toLong(),
        darkTheme = darkTheme,
        paletteStyle = PaletteStyle.Vibrant,
        specVersion = ColorSpec.SpecVersion.Default
    ) {
        val emojiList = listOf(
            "🌏", "🪲", "⚙️", "😍", "🖼️", "❤️", "😆", "🔥", "🌈", "🍎", "⚽", "🚗", "📱", "💻", "⌚",
            "🎧", "📷", "💡", "🔑", "🎁", "🎈", "🎉", "🎨", "🎭", "🎮", "🎲", "🎯", "🎳"
        )

        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(MaterialTheme.spacing.extraLarge)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ){
            EmojiAvatar(
                emoji = emojiList[currentEmojiIndex],
                rotation = rotation,
                containerSize = 100.dp,
                fontSize = 40.sp
            )

            Slider(
                value = rotation,
                onValueChange = { rotation = it },
                valueRange = 0f..180f
            )

            Button(
                onClick = {
                    currentEmojiIndex = (currentEmojiIndex + 1) % emojiList.size
                }
            ) {
                Text("Next emoji")
            }

            ToggleButton(
                checked = darkTheme,
                onCheckedChange = { darkTheme = it }
            ) {
                Text(if (darkTheme) "Dark Theme" else "Light Theme")
            }

        }


    }
}
