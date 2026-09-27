package com.dertefter.design.components.avatar

import android.graphics.Canvas
import android.graphics.Paint
import android.util.LruCache
import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
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
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.dertefter.design.components.common.RoundedPolygonShape
import com.dertefter.design.theme.WearableTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

val EmojiColorCache = LruCache<String, Color>(200)

private fun String.isUrl(): Boolean {
    return Patterns.WEB_URL.matcher(this).matches()
}

@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    data: String = "",
    emoji: String = data,
    containerSize: Dp = 52.dp,
    rotation: Float = 0f,
    fontSize: TextUnit? = null,
    onClick: () -> Unit = {},
) {
    val avatarData = data.ifEmpty { emoji }
    if (avatarData.isUrl()) {
        ImageAvatar(
            modifier = modifier,
            url = avatarData,
            containerSize = containerSize,
            rotation = rotation,
            onClick = onClick
        )
    } else {
        EmojiAvatar(
            modifier = modifier,
            emoji = avatarData,
            containerSize = containerSize,
            rotation = rotation,
            fontSize = fontSize,
            onClick = onClick
        )
    }
}

@Composable
fun ImageAvatar(
    modifier: Modifier = Modifier,
    url: String,
    containerSize: Dp = 52.dp,
    rotation: Float = 0f,
    onClick: () -> Unit = {},
) {
    val polygonParameters =
        remember(url) { getEmojiPolygonParameters(url, onlyEvenNumVertices = true) }

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

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(containerSize)
                .graphicsLayer { rotationZ = rotation }
                .clip(clip)
                .clickable(
                    onClick = onClick,
                    interactionSource = interactionSource,
                    indication = null
                )
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun EmojiAvatar(
    modifier: Modifier = Modifier,
    emoji: String,
    containerSize: Dp = 52.dp,
    rotation: Float = 0f,
    fontSize: TextUnit? = null,
    onClick: () -> Unit = {},
) {
    val fallbackColor = MaterialTheme.colorScheme.surfaceContainer
    val fallbackEmojiColor = MaterialTheme.colorScheme.onSurfaceVariant

    var baseEmojiColor by remember(emoji) { mutableStateOf<Color?>(null) }

    LaunchedEffect(emoji) {
        baseEmojiColor = extractEmojiColor(
            emoji = emoji,
            defaultColorInt = Color.Transparent.toArgb()
        )
    }

    val harmonizedBgColor by remember(
        baseEmojiColor,
        fallbackColor
    ) {
        derivedStateOf {
            val color = baseEmojiColor ?: return@derivedStateOf fallbackColor
            lerp(color, fallbackColor, 0.76f)
        }
    }

    val harmonizedEmojiColor by remember(baseEmojiColor, fallbackColor, fallbackEmojiColor) {
        derivedStateOf {
            val color = baseEmojiColor ?: return@derivedStateOf fallbackColor
            lerp(color, fallbackEmojiColor, 0.1f)
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

    val isInspection = LocalInspectionMode.current

    val dynamicFontSize = fontSize ?: (containerSize.value * 0.4f).sp

    val textStyle = remember(harmonizedEmojiColor, isInspection, dynamicFontSize) {
        TextStyle(
            fontSize = dynamicFontSize,
            color = harmonizedEmojiColor,
            shadow = Shadow(
                color = harmonizedEmojiColor,
                blurRadius = 20f
            ),
        )
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(containerSize)
                .graphicsLayer { rotationZ = rotation }
                .clip(clip)
                .clickable(
                    onClick = onClick,
                    interactionSource = interactionSource,
                    indication = null
                )
                .background(harmonizedBgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                maxLines = 1,
                modifier = Modifier
                    .graphicsLayer { rotationZ = -rotation },
                textAlign = TextAlign.Center,
                style = textStyle,
                color = harmonizedEmojiColor
            )
        }
    }
}

private fun getEmojiPolygonParameters(
    str: String,
    onlyEvenNumVertices: Boolean = false
): Triple<Int, Float, Float> {
    val random = Random(str.hashCode().toLong())

    val numVertices = if (onlyEvenNumVertices) {
        random.nextInt(from = 2, until = 7) * 2 // 4..12
    } else {
        random.nextInt(from = 3, until = 13) // 3..12
    }

    val innerRadius = 0.3f + random.nextFloat() * 0.3f
    val rounding = 0.1f + (1f - random.nextFloat()) * 0.7f

    return Triple(numVertices, innerRadius, rounding)
}

private suspend fun extractEmojiColor(
    emoji: String,
    defaultColorInt: Int
): Color = withContext(Dispatchers.Default) {
    EmojiColorCache.get(emoji)?.let { return@withContext it }
    val colorInt = runCatching {
        val size = 64
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
            palette.getVibrantColor(
                palette.getDominantColor(
                    palette.getMutedColor(
                        palette.getDarkMutedColor(
                            palette.getLightVibrantColor(defaultColorInt)
                        )
                    )
                )
            )
        )
    }.getOrDefault(defaultColorInt)
    val result = Color(colorInt)
    EmojiColorCache.put(emoji, result)
    result
}

@Preview
@Composable
private fun ImageAvatarPreview() {
    WearableTheme {
        ImageAvatar(
            containerSize = 48.dp,
            rotation = 0f,
            url = "https://cdn.xn--d1ah4a.com/images/avatars/44f5f3ed-1d7e-4441-ade1-5ff241c0baab.jpg"
        )
    }
}

@Preview
@Composable
fun PreviewAv() {
    WearableTheme(
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
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background)
                .padding(2.dp)
        ) {
            items(emojiList) { emoji ->
                Avatar(data = emoji, containerSize = 40.dp)
            }
        }
    }
}
