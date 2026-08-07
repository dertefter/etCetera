package com.dertefter.design.components.post

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.dertefter.design.R
import com.dertefter.design.theme.AppTheme
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PrettyDate(
    modifier: Modifier = Modifier,
    createdDate: LocalDateTime?,
    editedDate: LocalDateTime? = null,
    textStyle: TextStyle = MaterialTheme.typography.labelLargeEmphasized,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
        alpha = 0.7f
    )
) {
    val date = editedDate ?: createdDate ?: return

    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(date) {
        now = LocalDateTime.now()
        while (true) {
            delay(60_000L.milliseconds)
            now = LocalDateTime.now()
        }
    }

    val display = remember(date, now) {
        getPrettyDateDisplay(date, now)
    }

    val dateText = when (display) {
        is DateDisplay.Now -> stringResource(R.string.design_date_now)
        is DateDisplay.Minutes -> stringResource(R.string.design_date_minutes_short, display.value)
        is DateDisplay.Hours -> stringResource(R.string.design_date_hours_short, display.value)
        is DateDisplay.Days -> stringResource(R.string.design_date_days_short, display.value)
        is DateDisplay.Weeks -> stringResource(R.string.design_date_weeks_short, display.value)
        is DateDisplay.Full -> display.text
    }

    val text = if (editedDate != null) {
        "$dateText ${stringResource(R.string.design_date_edited)}"
    } else {
        dateText
    }

    val floatSpec = MaterialTheme.motionScheme.slowEffectsSpec<Float>()
    val intOffsetSpec = MaterialTheme.motionScheme.slowEffectsSpec<IntOffset>()

    AnimatedContent(
        targetState = text,
        transitionSpec = {
            (slideInVertically(intOffsetSpec) { it } + fadeIn(floatSpec)) togetherWith
                    (slideOutVertically(intOffsetSpec) { -it } + fadeOut(floatSpec))
        },
        label = "pretty_date",
        modifier = modifier
    ) { targetText ->
        Text(
            text = targetText,
            style = textStyle,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End
        )
    }
}

private sealed class DateDisplay {
    object Now : DateDisplay()
    data class Minutes(val value: Long) : DateDisplay()
    data class Hours(val value: Long) : DateDisplay()
    data class Days(val value: Long) : DateDisplay()
    data class Weeks(val value: Long) : DateDisplay()
    data class Full(val text: String) : DateDisplay()
}

private fun getPrettyDateDisplay(date: LocalDateTime, now: LocalDateTime): DateDisplay {
    val duration = Duration.between(date, now)
    val seconds = duration.seconds

    return when {
        seconds < 60 -> DateDisplay.Now
        seconds < 3600 -> DateDisplay.Minutes(seconds / 60)
        seconds < 86400 -> DateDisplay.Hours(seconds / 3600)
        seconds < 604800 -> DateDisplay.Days(seconds / 86400)
        seconds < 2419200 -> DateDisplay.Weeks(seconds / 604800)
        date.isBefore(now.minusYears(1)) -> {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
            DateDisplay.Full(date.format(formatter))
        }
        else -> {
            val pattern = if (date.year == now.year) "d MMMM" else "d MMMM yyyy"
            val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
            DateDisplay.Full(date.format(formatter).replace(".", ""))
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PrettyDatePrev() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PrettyDate(createdDate = LocalDateTime.now(), editedDate = null)
            PrettyDate(createdDate = LocalDateTime.now().minusMinutes(5), editedDate = LocalDateTime.now().minusMinutes(2))
            PrettyDate(createdDate = LocalDateTime.now().minusHours(8), editedDate = null)
            PrettyDate(createdDate = LocalDateTime.now().minusDays(2), editedDate = LocalDateTime.now().minusDays(1))
            PrettyDate(createdDate = LocalDateTime.now().minusWeeks(3), editedDate = null)
            PrettyDate(createdDate = LocalDateTime.now().minusMonths(2), editedDate = null)
            PrettyDate(createdDate = LocalDateTime.now().minusYears(1), editedDate = null)
        }
    }
}
