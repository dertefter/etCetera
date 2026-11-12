package com.dertefter.feed.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hashtags: List<String> = emptyList(),
    onClick: () -> Unit = {}
){
    var currentIndex by remember(hashtags) { mutableIntStateOf(0) }

    val hashtags = hashtags.plus("Поиск...")

    LaunchedEffect(hashtags) {
        if (hashtags.isNotEmpty()) {
            while (true) {
                delay(3.seconds)
                currentIndex = (currentIndex + 1) % hashtags.size
            }
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = CircleShape,
        onClick = onClick
    ){
        Row(
            modifier = Modifier
                .padding(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.small)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                imageVector = Icons.Search,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            val currentTag = hashtags.getOrElse(currentIndex) { hashtags.first() }

            AnimatedContent(
                targetState = currentTag,
                transitionSpec = {
                    (slideInVertically { height -> height } + fadeIn()) togetherWith
                            (slideOutVertically { height -> -height } + fadeOut())
                },
                modifier = Modifier.weight(1f),
                label = "SearchButtonHashtagAnimation"
            ) { tag ->
                Text(
                    text = tag,
                    maxLines = 1,
                    style = MaterialTheme.typography.headlineSmall,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

}

@Preview
@Composable
fun SearchBarPreview() {
    AppTheme {
        SearchBar(
            modifier = Modifier,
            hashtags = listOf("android", "kotlin", "compose")
        )
    }
}

@Preview
@Composable
fun SearchBarEmptyPreview() {
    AppTheme {
        SearchBar(
            modifier = Modifier,
            hashtags = emptyList()
        )
    }
}

