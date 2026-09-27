package com.dertefter.design.components.avatar

import android.graphics.Canvas
import android.graphics.Paint
import android.util.LruCache
import android.util.Patterns
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.material3.toShape
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
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.palette.graphics.Palette
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.dertefter.design.components.common.RoundedPolygonShape
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.EmojiAvatarHarmonizationColor
import com.dertefter.design.theme.emojiAvatarHarmonizeColor
import com.materialkolor.ktx.harmonize
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
    data: String,
    containerSize: Dp = 52.dp,
    rotation: Float = 0f,
    onClick: () -> Unit = {},
) {
    if (data.isUrl()) {
        ImageAvatar(
            modifier,
            data,
            containerSize,
            rotation,
            onClick
        )
    } else {
        EmojiAvatar(
            modifier,
            data,
            containerSize,
            rotation,
            onClick
        )
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(containerSize)
                .clip(polygon.toShape(startAngle = rotation.toInt()))
                .clickable(
                    onClick = onClick,
                    indication = ripple(
                        color = MaterialTheme.colorScheme.outline
                    ),
                    interactionSource = interactionSource
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EmojiAvatar(
    modifier: Modifier = Modifier,
    emoji: String,
    containerSize: Dp = 52.dp,
    rotation: Float = 0f,
    onClick: () -> Unit = {},
) {


    val targetBgColor by animateColorAsState(
        when (MaterialTheme.emojiAvatarHarmonizeColor) {
            EmojiAvatarHarmonizationColor.PRIMARY -> MaterialTheme.colorScheme.primary
            EmojiAvatarHarmonizationColor.SECONDARY -> MaterialTheme.colorScheme.secondary
            EmojiAvatarHarmonizationColor.TERTIARY -> MaterialTheme.colorScheme.tertiary
            EmojiAvatarHarmonizationColor.SURFACE_CONTAINER -> MaterialTheme.colorScheme.surfaceContainerHigh
            EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER -> MaterialTheme.colorScheme.primaryContainer
            EmojiAvatarHarmonizationColor.SECONDARY_CONTAINER -> MaterialTheme.colorScheme.secondaryContainer
            EmojiAvatarHarmonizationColor.TERTIARY_CONTAINER -> MaterialTheme.colorScheme.tertiaryContainer
            else -> Color.Unspecified
        }
    )

    val targetEmojiColor by animateColorAsState(
        when (MaterialTheme.emojiAvatarHarmonizeColor) {
            EmojiAvatarHarmonizationColor.PRIMARY -> MaterialTheme.colorScheme.onPrimary
            EmojiAvatarHarmonizationColor.SECONDARY -> MaterialTheme.colorScheme.onSecondary
            EmojiAvatarHarmonizationColor.TERTIARY -> MaterialTheme.colorScheme.onTertiary
            EmojiAvatarHarmonizationColor.SURFACE_CONTAINER -> MaterialTheme.colorScheme.onSurfaceVariant
            EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER -> MaterialTheme.colorScheme.onPrimaryContainer
            EmojiAvatarHarmonizationColor.SECONDARY_CONTAINER -> MaterialTheme.colorScheme.onSecondaryContainer
            EmojiAvatarHarmonizationColor.TERTIARY_CONTAINER -> MaterialTheme.colorScheme.onTertiaryContainer
            else -> Color.Transparent
        }
    )

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
        targetBgColor,
        fallbackColor
    ) {
        derivedStateOf {
            val color = baseEmojiColor ?: return@derivedStateOf fallbackColor
            if (targetBgColor == Color.Unspecified) lerp(color, fallbackColor, 0.76f)
            else color.harmonize(targetBgColor, matchSaturation = true)
        }
    }

    val harmonizedEmojiColor by remember(baseEmojiColor, targetEmojiColor, fallbackColor) {
        derivedStateOf {
            val color = baseEmojiColor ?: return@derivedStateOf fallbackColor
            if (targetBgColor == Color.Unspecified) lerp(color, fallbackEmojiColor, 0.1f)
            else color.harmonize(targetEmojiColor, matchSaturation = true)
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


    val dynamicFontSize = (containerSize.value * 0.4f).sp


    val textStyle = remember(harmonizedEmojiColor, isInspection) {
        TextStyle(
            fontSize = dynamicFontSize,
            //fontFamily = fontFamily,
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

@Preview(showBackground = true)
@Composable
private fun ImageAvatarPreview() {
    AppTheme {
        ImageAvatar(
            containerSize = 56.dp,
            rotation = 0f,
            url = "https://cdn.xn--d1ah4a.com/images/avatars/44f5f3ed-1d7e-4441-ade1-5ff241c0baab.jpg"
        )
    }
}

@Preview(
    showBackground = true,
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE
)
@Composable
private fun EmojiAvatarPreview() {
    AppTheme(
        emojiAvatarHarmonizeColor = EmojiAvatarHarmonizationColor.DEFAULT
    ) {

        val emojiList = listOf(
            "🐞", "🐜", "🪰", "🪱", "🦗", "🕷️", "🕸️", "🦂", "🐢", "🐍",
            "🦎", "🦖", "🦕", "🐙", "🦑", "🦐", "🦞", "🦀", "🐡", "🐠",
            "🤥", "😌", "😔", "😪", "🤤", "😴", "😷", "🤒", "🤕", "🤢",
            "🦧", "🐘", "🦛", "🦏", "🐪", "🐫", "🦒", "🦘", "🐃", "🐂",
            "💩", "🤡", "👹", "👺", "👻", "👽", "👾", "🤖", "🎃", "😺",
            "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾", "🐼", "🐻‍❄️",
            "👋", "🤚", "🖐️", "✋", "🖖", "👌", "🤌", "🤏", "✌️", "🤞",
            "🤮", "🤧", "🥵", "🥶", "🥴", "😵", "🤯", "🤠", "🥳", "🥺",
            "😢", "😭", "😤", "😠", "😡", "🤬", "😈", "👿", "💀", "☠️",
            "🥲", "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭", "🤫",
            "🤔", "🤐", "🤨", "😐", "😑", "😶", "😏", "😒", "🙄", "😬",
            "🐒", "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉",
            "🦇", "🐺", "🐗", "🐴", "🦄", "🐝", "🪱", "🐛", "🦋", "🐌",
            "🐯", "🦁", "🐮", "🐷", "🐽", "🐸", "🐵", "🙈", "🙉", "🙊",
            "🐟", "🐬", "🐳", "🐋", "🦈", "🐊", "🐅", "🐆", "🦓", "🦍",
            "🤟", "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "👍",
            "👎", "✊", "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝",
            "🙏", "✍️", "💅", "🤳", "💪", "🦾", "🦿", "🦵", "🦶", "👂",
            "🦻", "👃", "🧠", "🫀", "🫁", "🦷", "🦴", "👀", "👁️", "👅",
            "👄", "💋", "🩸", "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻",
            "✅", "😺", "🦐", "🍃", "❌", "👻", "🦎", "😴", "💙", "🌴",
            "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃",
            "😉", "😊", "😇", "🥰", "😍", "🤩", "😘", "😗", "😚", "😙",
            "🐄", "🐎", "🐖", "🐏", "🐑", "🦙", "🐐", "🦌", "🐕", "🐩",
            "🦮", "🐕‍🦺", "🐈", "🐈‍⬛", "🪶", "🐓", "🦃", "🦤", "🦚", "🦜",
            "🦢", "🦩", "🕊️", "🐇", "🦝", "🦨", "🦡", "🦫", "🦦", "🦥",
            "🐁", "🐀", "🐿️", "🦔", "🐨", "🪵", "🌱", "🌿", "☘️", "🍀",
            "🎍", "🪴", "🎋", "🍃", "🍂", "🍁", "🍄", "🐚", "🪨", "🌾",
            "💐", "🌷", "🌹", "🥀", "🌺", "🌸", "🌼", "🌻", "🌞", "🌝",
            "🌛", "🌜", "🌚", "🌕", "🌖", "🌗", "🌘", "🌑", "🌒", "🌓",
            "🌔", "🌙", "🌎", "🌍", "🌏", "🪐", "💫", "⭐", "🌟", "✨",
            "⚡", "☄️", "💥", "🔥", "🌪️", "🌈", "☀️", "🌤️", "⛅", "🌥️",
            "☁️", "🌦️", "🌧️", "⛈️", "🌩️", "🌨️", "❄️", "☃️", "⛄", "🌬️",
            "💨", "💧", "💦", "☔", "☂️", "🌊", "🌫️", "🌵", "🎄", "🌲", "🌳",
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(5)
        ) {
            items(emojiList) { emoji ->
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    EmojiAvatar(
                        containerSize = 56.dp,
                        rotation = 0f,
                        emoji = emoji,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    )
                }

            }
        }

    }
}




