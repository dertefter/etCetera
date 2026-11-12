package com.dertefter.feed.presentation.components

import android.graphics.Canvas
import android.graphics.Paint
import android.util.LruCache
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.palette.graphics.Palette
import com.dertefter.data.dto.search.TopClanDto
import com.dertefter.design.common.prettifyInt
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.EmojiAvatarHarmonizationColor
import com.dertefter.design.theme.emojiAvatarHarmonizeColor
import com.dertefter.design.theme.spacing
import com.materialkolor.ktx.harmonize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val TopClanEmojiColorCache = LruCache<String, Color>(200)

@Composable
fun TopClanItem(
    modifier: Modifier = Modifier,
    clanDto: TopClanDto,
    onClick: () -> Unit = {},
    isCountVisible: Boolean = true
){

    val emojiAvatarHarmonizeColor = MaterialTheme.emojiAvatarHarmonizeColor

    val containerColor = when (emojiAvatarHarmonizeColor) {
        EmojiAvatarHarmonizationColor.PRIMARY -> MaterialTheme.colorScheme.primary
        EmojiAvatarHarmonizationColor.SECONDARY -> MaterialTheme.colorScheme.secondary
        EmojiAvatarHarmonizationColor.TERTIARY -> MaterialTheme.colorScheme.tertiary
        EmojiAvatarHarmonizationColor.SURFACE_CONTAINER -> MaterialTheme.colorScheme.surfaceContainer
        EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER -> MaterialTheme.colorScheme.primaryContainer
        EmojiAvatarHarmonizationColor.SECONDARY_CONTAINER -> MaterialTheme.colorScheme.secondaryContainer
        EmojiAvatarHarmonizationColor.TERTIARY_CONTAINER -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainer
    }

    val contentColor = when (emojiAvatarHarmonizeColor) {
        EmojiAvatarHarmonizationColor.PRIMARY -> MaterialTheme.colorScheme.onPrimary
        EmojiAvatarHarmonizationColor.SECONDARY -> MaterialTheme.colorScheme.onSecondary
        EmojiAvatarHarmonizationColor.TERTIARY -> MaterialTheme.colorScheme.onTertiary
        EmojiAvatarHarmonizationColor.SURFACE_CONTAINER -> MaterialTheme.colorScheme.onSurfaceVariant
        EmojiAvatarHarmonizationColor.PRIMARY_CONTAINER -> MaterialTheme.colorScheme.onPrimaryContainer
        EmojiAvatarHarmonizationColor.SECONDARY_CONTAINER -> MaterialTheme.colorScheme.onSecondaryContainer
        EmojiAvatarHarmonizationColor.TERTIARY_CONTAINER -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    var baseEmojiColor by remember(clanDto.avatar) { mutableStateOf<Color?>(null) }

    LaunchedEffect(clanDto.avatar) {
        baseEmojiColor = extractEmojiColor(
            emoji = clanDto.avatar,
            defaultColorInt = Color.Transparent.toArgb()
        )
    }

    val harmonizedContainerColor by remember(
        baseEmojiColor,
        containerColor,
        emojiAvatarHarmonizeColor
    ) {
        derivedStateOf {
            val color = baseEmojiColor ?: return@derivedStateOf containerColor
            if (emojiAvatarHarmonizeColor == EmojiAvatarHarmonizationColor.DEFAULT) color.copy(alpha = 0.3f)
            else color.harmonize(containerColor, matchSaturation = true)
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    val harmonizedContentColor by remember(
        baseEmojiColor,
        contentColor,
        emojiAvatarHarmonizeColor
    ) {
        derivedStateOf {
            val color = baseEmojiColor ?: return@derivedStateOf contentColor
            if (emojiAvatarHarmonizeColor == EmojiAvatarHarmonizationColor.DEFAULT) color.harmonize(primaryColor, matchSaturation = true)
            else color.harmonize(contentColor, matchSaturation = true)
        }
    }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .clickable(
                onClick = onClick
            )
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .background(harmonizedContainerColor)
            .padding(
                horizontal = MaterialTheme.spacing.large,
                vertical = MaterialTheme.spacing.medium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small + 2.dp)
    ){

        Text(
            style = MaterialTheme.typography.headlineSmall,
            text = clanDto.avatar,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        AnimatedVisibility(
            visible = isCountVisible
        ) {
            Text(
                style = MaterialTheme.typography.titleMediumEmphasized,
                text = clanDto.memberCount.prettifyInt(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = harmonizedContentColor
            )
        }

    }
}

private suspend fun extractEmojiColor(
    emoji: String,
    defaultColorInt: Int
): Color = withContext(Dispatchers.Default) {
    TopClanEmojiColorCache.get(emoji)?.let { return@withContext it }
    val colorInt = runCatching {
        val size = 128
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
    TopClanEmojiColorCache.put(emoji, result)
    result
}

@Preview
@Composable
fun TopClanItemPr(){
    AppTheme() {
        TopClanItem(
            clanDto = TopClanDto(
                avatar = "⏳",
                memberCount = 300
            ),
        )
    }
}