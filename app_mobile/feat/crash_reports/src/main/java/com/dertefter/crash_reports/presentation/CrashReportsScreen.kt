package com.dertefter.crash_reports.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.icons.Icons
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun CrashReportsScreen(
    onEvent: (Event) -> Unit,
    uiState: UiState,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val hazeState = rememberHazeState()

    if (uiState.selectedReportContent != null) {
        AlertDialog(
            onDismissRequest = { onEvent(Event.OnDismissDialog) },
            title = { Text("Детали лога") },
            text = {
                Box(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text = uiState.selectedReportContent)
                }
            },
            confirmButton = {
                TextButton(onClick = { onEvent(Event.OnDismissDialog) }) {
                    Text("Закрыть")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppTopBar(
                hazeState = hazeState,
                navigationIcon = {
                    IconButton(onClick = { onEvent(Event.OnBack) }) {
                        Icon(imageVector = Icons.ArrowBack, contentDescription = "Назад")
                    }
                },
                title = {
                    Text("Логи")
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { padding ->
        PullToRefreshBox(
            modifier = Modifier.padding(padding),
            isRefreshing = uiState.isLoading,
            onRefresh = { onEvent(Event.OnRefresh) }
        ) {
            if (uiState.reports.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Логов пока нет")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .hazeSource(hazeState)
                        .fillMaxSize()
                ) {
                    items(uiState.reports) { report ->
                        ListItem(
                            modifier = Modifier.clickable { onEvent(Event.OnClickReport(report.path)) },
                            supportingContent = {
                                Text(text = report.path)
                            },
                            trailingContent = {
                                Row {
                                    IconButton(onClick = { onEvent(Event.OnShareReport(report.path)) }) {
                                        Icon(imageVector = Icons.Share, contentDescription = "Поделиться")
                                    }
                                    IconButton(onClick = { onEvent(Event.OnDeleteReport(report.path)) }) {
                                        Icon(imageVector = Icons.Delete, contentDescription = "Удалить")
                                    }
                                }
                            }
                        ) {
                            Text(text = report.name)
                        }
                    }
                }
            }
        }
    }
}
