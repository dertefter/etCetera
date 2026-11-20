package com.dertefter.new_post.presentation.component

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.new_post.R
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val STEP_DATE = 0
private const val STEP_TIME = 1

@Composable
fun SchedulePostDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
) {
    val (initialHour, initialMinute) = remember {
        val soon = Calendar.getInstance().apply { add(Calendar.MINUTE, 10) }
        soon.get(Calendar.HOUR_OF_DAY) to ((soon.get(Calendar.MINUTE) / 5) * 5)
    }

    val todayUtcMillis = remember {
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = todayUtcMillis,
        selectableDates = remember {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val todayMidnightUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    return utcTimeMillis >= todayMidnightUtc
                }
            }
        }
    )
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
    )

    var step by remember { mutableIntStateOf(STEP_DATE) }

    val scheduledMillis = remember(
        datePickerState.selectedDateMillis,
        timePickerState.hour,
        timePickerState.minute
    ) {
        datePickerState.selectedDateMillis?.let { utcMillis ->
            toLocalMillis(utcMillis, timePickerState.hour, timePickerState.minute)
        }
    }

    val isTimeAlreadyPast = remember(scheduledMillis) {
        scheduledMillis == null || scheduledMillis <= System.currentTimeMillis()
    }

    val canProceed = when (step) {
        STEP_DATE -> datePickerState.selectedDateMillis != null
        else -> scheduledMillis != null && !isTimeAlreadyPast
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        val dialogWindowProvider = LocalView.current.parent as? DialogWindowProvider
        dialogWindowProvider?.window?.setDimAmount(0F)
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    Text(
                        text = stringResource(R.string.schedule_dialog_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    SchedulePostDialogContent(
                        step = step,
                        datePickerState = datePickerState,
                        timePickerState = timePickerState,
                        scheduledMillis = scheduledMillis,
                        isTimeAlreadyPast = isTimeAlreadyPast
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (step == STEP_DATE) {
                        TextButton(onClick = onDismiss) {
                            Text(text = stringResource(R.string.action_cancel))
                        }
                    } else {
                        TextButton(onClick = { step = STEP_DATE }) {
                            Text(text = stringResource(R.string.schedule_action_back))
                        }
                    }

                    TextButton(
                        enabled = canProceed,
                        onClick = {
                            when (step) {
                                STEP_DATE -> step = STEP_TIME
                                else -> scheduledMillis?.let(onConfirm)
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(
                                if (step == STEP_DATE) R.string.schedule_action_next
                                else R.string.schedule_action_confirm
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SchedulePostDialogContent(
    step: Int,
    datePickerState: DatePickerState,
    timePickerState: TimePickerState,
    scheduledMillis: Long?,
    isTimeAlreadyPast: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        Text(
            text = stringResource(
                if (step == STEP_DATE) R.string.schedule_step_date
                else R.string.schedule_step_time
            ),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        when (step) {
            STEP_DATE -> Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false
                )
            }
            else -> TimePicker(
                state = timePickerState,
                modifier = Modifier
                    .padding(top = MaterialTheme.spacing.small)
                    .align(Alignment.CenterHorizontally)
            )
        }

        if (scheduledMillis != null) {
            Text(
                text = stringResource(
                    R.string.schedule_selected_datetime,
                    formatDate(context, scheduledMillis),
                    formatClock(context, scheduledMillis)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (step == STEP_TIME && isTimeAlreadyPast) {
            Text(
                text = stringResource(R.string.schedule_error_time_in_past),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

private fun toLocalMillis(utcDateMillis: Long, hour: Int, minute: Int): Long {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = utcDateMillis
    }
    return Calendar.getInstance().apply {
        clear()
        set(
            utc.get(Calendar.YEAR),
            utc.get(Calendar.MONTH),
            utc.get(Calendar.DAY_OF_MONTH),
            hour,
            minute
        )
    }.timeInMillis
}

private fun formatDate(context: Context, millis: Long): String =
    DateFormat.getDateInstance(DateFormat.LONG, locale(context)).format(Date(millis))

private fun formatClock(context: Context, millis: Long): String =
    DateFormat.getTimeInstance(DateFormat.SHORT, locale(context)).format(Date(millis))

private fun locale(context: Context): Locale =
    context.resources.configuration.locales[0]

@Preview(showBackground = true)
@Composable
fun SchedulePostDialogContentPreview() {
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    AppTheme {
        Surface(
            modifier = Modifier.padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.schedule_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                SchedulePostDialogContent(
                    step = STEP_DATE,
                    datePickerState = datePickerState,
                    timePickerState = timePickerState,
                    scheduledMillis = System.currentTimeMillis(),
                    isTimeAlreadyPast = false,
                )
            }
        }
    }
}



