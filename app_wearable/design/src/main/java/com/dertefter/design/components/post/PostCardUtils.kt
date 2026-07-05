package com.dertefter.design.components.post

import androidx.wear.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun buildPostAnnotatedString(
    content: String,
    spans: List<SpanUiModel>,
    revealedSpoilers: Set<Int> = emptySet()
): AnnotatedString {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val surfaceVariant = MaterialTheme.colorScheme.surfaceContainerHigh

    return buildAnnotatedString {
        append(content)
        spans.forEachIndexed { index, span ->
            val start = span.offset
            val end = span.offset + span.length
            if (start in content.indices && end <= content.length) {
                when (span.type) {
                    "bold" -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                    "italic" -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                    "monospace" -> addStyle(SpanStyle(fontFamily = FontFamily.Monospace), start, end)
                    "strike" -> addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough), start, end)
                    "underline" -> addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
                    "spoiler" -> {
                        if (revealedSpoilers.contains(index)) {
                            addStyle(SpanStyle(background = surfaceVariant), start, end)
                        } else {
                            addStyle(
                                SpanStyle(
                                    background = onSurfaceVariant,
                                    color = Color.Transparent
                                ), start, end
                            )
                        }
                        addStringAnnotation("SPOILER", index.toString(), start, end)
                    }
                    "mention" -> {
                        addStyle(SpanStyle(color = primaryColor, fontWeight = FontWeight.Bold), start, end)
                        span.username?.let { addStringAnnotation("MENTION", it, start, end) }
                    }
                    "hashtag" -> {
                        addStyle(SpanStyle(color = primaryColor, fontWeight = FontWeight.Bold), start, end)
                        span.tag?.let { addStringAnnotation("HASHTAG", it, start, end) }
                    }
                }
            }
        }
    }
}
