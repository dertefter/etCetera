package com.dertefter.design.components.avatar

import android.graphics.Canvas
import android.graphics.Paint
import android.util.LruCache
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.dertefter.design.theme.EmojiAvatarHarmonizationColor
import com.dertefter.design.theme.emojiAvatarHarmonizeColor
import com.dertefter.design.theme.spacing
import com.materialkolor.ktx.harmonize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

val EmojiColorCache = LruCache<String, Color>(200)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EmojiAvatar(
    modifier: Modifier = Modifier,
    emoji: String,
    containerSize: Dp = 52.dp,
    fontSize: TextUnit = 20.sp,
    rotation: Float = 0f,
    onClick: () -> Unit = {},
    isOnline: Boolean = false
) {

    val targetColor by animateColorAsState(
        when (MaterialTheme.emojiAvatarHarmonizeColor) {
            EmojiAvatarHarmonizationColor.PRIMARY -> MaterialTheme.colorScheme.primary
            EmojiAvatarHarmonizationColor.SECONDARY -> MaterialTheme.colorScheme.secondary
            EmojiAvatarHarmonizationColor.TERTIARY -> MaterialTheme.colorScheme.tertiary
            EmojiAvatarHarmonizationColor.SURFACE_CONTAINER -> MaterialTheme.colorScheme.surfaceContainerHigh
            EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER -> MaterialTheme.colorScheme.primaryContainer
            EmojiAvatarHarmonizationColor.SECONDARY_CONTAINER -> MaterialTheme.colorScheme.secondaryContainer
            EmojiAvatarHarmonizationColor.TERTIARY_CONTAINER -> MaterialTheme.colorScheme.tertiaryContainer
        }
    )

    val targetBgColor = targetColor
    val targetShadowColor = MaterialTheme.colorScheme.onPrimaryFixed
    val fallbackColor = MaterialTheme.colorScheme.surfaceContainer

    var baseEmojiColor by remember(emoji) { mutableStateOf<Color?>(null) }

    LaunchedEffect(emoji) {
        baseEmojiColor = extractEmojiColor(
            emoji = emoji,
            defaultColorInt = Color.Transparent.toArgb()
        )
    }

    val harmonizedBgColor by remember(baseEmojiColor, targetBgColor, fallbackColor) {
        derivedStateOf {
            val color = baseEmojiColor ?: return@derivedStateOf fallbackColor
            if (color == Color.Transparent) fallbackColor
            else color.harmonize(targetBgColor, matchSaturation = true)
        }
    }

    val harmonizedShadowColor by remember(baseEmojiColor, targetShadowColor, fallbackColor) {
        derivedStateOf {
            val color = baseEmojiColor ?: return@derivedStateOf fallbackColor
            if (color == Color.Transparent) fallbackColor
            else color.harmonize(targetShadowColor, matchSaturation = true)
        }
    }



    val polygonParameters = remember(emoji) { getEmojiPolygonParameters(emoji) }

    val polygon = remember(polygonParameters) {
        RoundedPolygon.star(
            numVerticesPerRadius = polygonParameters.first,
            innerRadius = polygonParameters.second,
            rounding = CornerRounding(polygonParameters.third)
        )
    }

    val clip = remember(polygon) {
        RoundedPolygonShape(polygon = polygon)
    }


    val textStyle = remember(harmonizedShadowColor) {
        TextStyle(
            shadow = Shadow(
                color = harmonizedShadowColor,
                blurRadius = 16f
            )
        )
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
    ){
        Box(
            modifier = Modifier
                .size(containerSize)
                .graphicsLayer { rotationZ = rotation }
                .clip(clip)
                .clickable(
                    onClick = onClick,
                    indication = ripple(
                        color = baseEmojiColor ?: MaterialTheme.colorScheme.outline
                    ),
                    interactionSource = interactionSource
                )
                .background(harmonizedBgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                maxLines = 1,
                fontSize = fontSize,
                modifier = Modifier.graphicsLayer { rotationZ = -rotation },
                textAlign = TextAlign.Center,
                style = textStyle
            )
        }


        AnimatedVisibility(
            visible = isOnline,
            modifier = Modifier
                .align(
                    Alignment.BottomEnd
                )
                .padding(MaterialTheme.spacing.medium)
        ) {
            Badge {}
        }
    }




}


private fun getEmojiPolygonParameters(emoji: String): Triple<Int, Float, Float> {
    val random = Random(emoji.hashCode().toLong())
    val numVertices = (random.nextInt(3) + 7)
    val innerRadius = 0.2f + random.nextFloat() * 0.5f
    val rounding = 0.2f + random.nextFloat() * 0.5f
    return Triple(numVertices,innerRadius,rounding)
}

private suspend fun extractEmojiColor(
    emoji: String,
    defaultColorInt: Int
): Color = withContext(Dispatchers.Default) {
    EmojiColorCache.get(emoji)?.let { return@withContext it }
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
                    palette.getDarkVibrantColor(defaultColorInt)
                )
            )
        )
    }.getOrDefault(defaultColorInt)
    val result = Color(colorInt)
    EmojiColorCache.put(emoji, result)
    result
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun PreviewAv() {
    var darkTheme by remember { mutableStateOf(true) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var currentEmojiIndex by remember { mutableIntStateOf(0) }

    AppTheme(
        darkTheme = darkTheme,
    ) {
        val emojiList = listOf(
            "🖼️", "🪲", "⚙️", "😍", "🖼️", "❤️", "😆", "🔥", "🌈", "🍎", "⚽", "🚗", "📱", "💻", "⌚",
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
        ) {
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
