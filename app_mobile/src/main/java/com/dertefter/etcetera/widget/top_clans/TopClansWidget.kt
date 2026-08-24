package com.dertefter.etcetera.widget.top_clans

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import androidx.annotation.ColorInt
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.core.util.TypedValueCompat.spToPx
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.PreviewSizeMode
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.palette.graphics.Palette
import com.dertefter.data.dto.search.TopClanDto
import com.dertefter.data.repository.SearchRepository
import com.dertefter.design.components.avatar.EmojiColorCache
import com.dertefter.etcetera.MainActivity
import com.dertefter.etcetera.R
import com.materialkolor.ktx.harmonize
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun searchRepository(): SearchRepository
}

class TopClansWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override val previewSizeMode: PreviewSizeMode = SizeMode.Responsive(
        setOf(DpSize(120.dp, 120.dp), DpSize(240.dp, 240.dp))
    )

    companion object {
        val IS_LOADING = booleanPreferencesKey("is_loading")

        val outCornerRadius = 32.dp
        val spaceBetweenItems = 3.dp
        val innerPadding = 12.dp
        val fadingEdgeLength = 24.dp
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        ).searchRepository()

        val clans = repository.getTopClans().firstOrNull() ?: emptyList()

        provideContent {
            val prefs = currentState<Preferences>()
            val isLoading = prefs[IS_LOADING] ?: false
            GlanceTheme {
                ClansWidgetContent(clans, isLoading)
            }
        }
    }

    @Composable
    internal fun ClansWidgetContent(clans: List<TopClanDto>, isLoading: Boolean = false) {
        val size = LocalSize.current
        val itemWidth = 120.dp
        val columns = floor(size.width / itemWidth).toInt().coerceIn(1, 5)

        val backgroundColor = GlanceTheme.colors.background

        Box(
            modifier = GlanceModifier
                .cornerRadius(outCornerRadius)
                .fillMaxSize()
                .background(backgroundColor)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.BottomCenter
        ){
            if (isLoading) {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GlanceTheme.colors.primary)
                }
            }
            else if (clans.isEmpty()) {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.app_widget_top_clans_empty),
                        style = TextStyle(color = GlanceTheme.colors.onBackground)
                    )
                }
            }
            else {
                LazyVerticalGrid(
                    gridCells = GridCells.Fixed(columns),
                    modifier = GlanceModifier
                        .fillMaxSize()
                ) {
                    itemsIndexed(clans) { index, clan ->

                        val isFirstRow = index < columns
                        val isFirstColumn = index % columns == 0
                        val isLastRow = index >= ((clans.count() - 1) / columns) * columns
                        val isLastColumn = index % columns == columns - 1

                        val topPadding = if (isFirstRow) innerPadding + 4.dp else spaceBetweenItems
                        val startPadding = if (isFirstColumn) innerPadding else spaceBetweenItems
                        val endPadding = if (isLastColumn) innerPadding else spaceBetweenItems
                        val bottomPadding = if (isLastRow) innerPadding + fadingEdgeLength else spaceBetweenItems

                        Box(
                            modifier = GlanceModifier.padding(
                                top = topPadding,
                                start = startPadding,
                                end = endPadding,
                                bottom = bottomPadding

                            )
                        ){
                            ClanItem(clan, modifier = GlanceModifier
                                .fillMaxWidth())
                        }

                    }
                }
            }

            Image(
                provider = ImageProvider(R.drawable.widget_fading_edge_gradient),
                contentDescription = null,
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(fadingEdgeLength),
                colorFilter = ColorFilter.tint(backgroundColor)
            )
        }


    }

    @Composable
    internal fun ClanItem(clan: TopClanDto, modifier: GlanceModifier) {

        val context = LocalContext.current
        val secondaryColor = GlanceTheme.colors.secondary

        val sourceColor = extractEmojiColor(
            clan.avatar,
            defaultColorInt = secondaryColor.getColor(context, isNightMode = false).toArgb()
        )

        val containerColor = androidx.glance.color.ColorProvider(
            day = sourceColor.harmonize(
                GlanceTheme.colors.primaryContainer.getColor(context, isNightMode = false),
                true
            ),
            night = sourceColor.harmonize(
                GlanceTheme.colors.primaryContainer.getColor(context, isNightMode = true),
                true
            )
        )

        val contentColor = androidx.glance.color.ColorProvider(
            day = sourceColor.harmonize(
                GlanceTheme.colors.onPrimaryContainer.getColor(context, isNightMode = false),
                true
            ),
            night = sourceColor.harmonize(
                GlanceTheme.colors.onPrimaryContainer.getColor(context, isNightMode = true),
                true
            )
        )

        Row(
            modifier = modifier
                .cornerRadius(100.dp)
                .padding(6.dp)
                .background(containerColor),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                provider = ImageProvider(
                    context.textAsBitmap(
                        text = clan.avatar,
                        fontSize = 24.sp,
                        shadowRadius = 4f,
                        shadowColor = contentColor.getColor(LocalContext.current).toArgb()
                    )
                ),
                contentDescription = null,
                modifier = GlanceModifier
                    .padding(6.dp)
            )

            GlanceText(
                modifier = GlanceModifier.padding(horizontal = 6.dp),
                text = formatCount(clan.postsCount),
                color = contentColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    private fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> String.format(Locale.US, "%.1fM", count / 1_000_000.0)
            count >= 1_000 -> String.format(Locale.US, "%.1fk", count / 1_000.0)
            else -> count.toString()
        }
    }
}

private fun extractEmojiColor(
    emoji: String,
    @ColorInt defaultColorInt: Int
): Color  {
    EmojiColorCache.get(emoji)?.let { return it }
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
    return result
}

private fun ColorProvider.getColor(context: Context, isNightMode: Boolean): Color {
    val configuration = Configuration(context.resources.configuration)
    configuration.uiMode = if (isNightMode) {
        Configuration.UI_MODE_NIGHT_YES
    } else {
        Configuration.UI_MODE_NIGHT_NO
    }
    val themeContext = context.createConfigurationContext(configuration)
    return this.getColor(themeContext)
}

fun Context.textAsBitmap(
    text: String,
    fontSize: TextUnit,
    color: Color = Color.Black,
    fontWeight: FontWeight = FontWeight.Normal,
    shadowRadius: Float = 0f,
    shadowDx: Float = 0f,
    shadowDy: Float = 0f,
    shadowColor: Int = 0x80000000.toInt()
): Bitmap {
    val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    paint.textSize = spToPx(fontSize.value, this.resources.displayMetrics)
    paint.color = color.toArgb()
    paint.typeface = Typeface.create(Typeface.DEFAULT, fontWeight.value, false)

    if (shadowRadius > 0f) {
        paint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)
    }

    val baseline = -paint.ascent()
    val textWidth = paint.measureText(text).toInt()
    val textHeight = (baseline + paint.descent()).toInt()

    val padding = (shadowRadius + max(abs(shadowDx), abs(shadowDy))).toInt()
    val bitmapWidth = textWidth + padding * 2
    val bitmapHeight = textHeight + padding * 2

    val image = createBitmap(bitmapWidth, bitmapHeight)
    val canvas = Canvas(image)
    canvas.drawText(text, padding.toFloat(), baseline + padding, paint)
    return image
}


@Composable
fun GlanceText(
    text: String,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    modifier: GlanceModifier = GlanceModifier,
    color: ColorProvider = FixedColorProvider(Color.Black)
) {
    Image(
        modifier = modifier,
        provider = ImageProvider(
            LocalContext.current.textAsBitmap(
                text = text,
                fontSize = fontSize,
                color = Color.White,
                fontWeight = fontWeight
            )
        ),
        contentDescription = null,
        colorFilter = ColorFilter.tint(color)
    )
}

private class FixedColorProvider(val color: Color) : ColorProvider {
    override fun getColor(context: Context): Color = color
}

@Suppress("unused")
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 200, heightDp = 300)
@Preview(widthDp = 160, heightDp = 160)
@Preview(widthDp = 300, heightDp = 200)
@Composable
fun TopClansWidgetPreview() {
    GlanceTheme {
        TopClansWidget().ClansWidgetContent(
            clans = listOf(
                TopClanDto(avatar = "\uD83E\uDD8E", postsCount = 50695),
                TopClanDto(avatar = "\uD83E\uDD21", postsCount = 24019),
                TopClanDto(avatar = "\uD83C\uDF45", postsCount = 19736),
                TopClanDto(avatar = "\uD83C\uDF4C", postsCount = 16535),
                TopClanDto(avatar = "\uD83D\uDC7E", postsCount = 15730),
                TopClanDto(avatar = "\uD83D\uDC80", postsCount = 14394),
                TopClanDto(avatar = "\uD83D\uDE0E", postsCount = 12855),
                TopClanDto(avatar = "⚙️", postsCount = 12500),
                TopClanDto(avatar = "🍃", postsCount = 12500),
                TopClanDto(avatar = "⚔️", postsCount = 12500),
            )
        )
    }
}
