package com.dertefter.etcetera.widget.tranding_hashtags

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
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
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.dertefter.data.dto.search.SearchHashtagDto
import com.dertefter.etcetera.MainActivity
import com.dertefter.etcetera.R
import com.dertefter.etcetera.di.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.firstOrNull
import java.util.Locale
import kotlin.math.floor


class TrendingHashtagsWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    companion object {
        val IS_LOADING = booleanPreferencesKey("is_loading")

        val outCornerRadius = 32.dp
        val spaceBetweenItems = 3.dp
        val innerPadding = 10.dp
        val fadingEdgeLength = innerPadding
        val itemCornerRadius = outCornerRadius - innerPadding

    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        ).searchRepository()

        val hashtags = repository.getTrendingHashtags().firstOrNull() ?: emptyList()

        provideContent {
            val prefs = currentState<Preferences>()
            val isLoading = prefs[IS_LOADING] ?: false
            GlanceTheme {
                HashtagsWidgetContent(hashtags, isLoading)
            }
        }
    }

    @Composable
    internal fun HashtagsWidgetContent(hashtags: List<SearchHashtagDto>, isLoading: Boolean = false) {
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
            else if (hashtags.isEmpty()) {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.app_widget_trending_hashtags_empty),
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
                    itemsIndexed(hashtags) { index, hashtag ->

                        val isFirstRow = index < columns
                        val isFirstColumn = index % columns == 0
                        val isLastRow = index >= ((hashtags.count() - 1) / columns) * columns
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
                            HashtagItem(hashtag, modifier = GlanceModifier
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
    internal fun HashtagItem(hashtag: SearchHashtagDto, modifier: GlanceModifier) {
        val context = LocalContext.current
        Column(
            modifier = modifier
                .cornerRadius(itemCornerRadius)
                .padding(14.dp)
                .background(GlanceTheme.colors.primaryContainer),
        ) {
            Text(
                text = "#${hashtag.name}",
                style = TextStyle(
                    color = GlanceTheme.colors.onPrimaryContainer,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 1,
            )

            Text(
                text = formatCount(context, hashtag.postsCount),
                style = TextStyle(
                    color = GlanceTheme.colors.onPrimaryContainer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )
        }
    }

    private fun formatCount(context: Context, count: Int): String {
        val formattedNumber = when {
            count >= 1_000_000 -> String.format(Locale.US, "%.1fM", count / 1_000_000.0)
            count >= 1_000 -> String.format(Locale.US, "%.1fk", count / 1_000.0)
            else -> count.toString()
        }
        return context.resources.getQuantityString(
            R.plurals.app_widget_trending_hashtags_posts_count,
            count,
            formattedNumber
        )
    }
}

@Suppress("unused")
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 200, heightDp = 300)
@Preview(widthDp = 160, heightDp = 160)
@Preview(widthDp = 300, heightDp = 200)
@Composable
fun TrendingHashtagsWidgetPreview() {
    GlanceTheme {
        TrendingHashtagsWidget().HashtagsWidgetContent(
            hashtags = listOf(
                SearchHashtagDto(id = "1", name = "арт", postsCount = 48506),
                SearchHashtagDto(id = "2", name = "мем", postsCount = 33261),
                SearchHashtagDto(id = "3", name = "итд", postsCount = 30311),
                SearchHashtagDto(id = "4", name = "art", postsCount = 26532),
                SearchHashtagDto(id = "5", name = "42", postsCount = 11077),
                SearchHashtagDto(id = "6", name = "мемы", postsCount = 9878),
                SearchHashtagDto(id = "7", name = "аниме", postsCount = 8916),
            )
        )
    }
}
