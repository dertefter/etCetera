package com.dertefter.crash_reports.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
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
import androidx.compose.ui.unit.dp
import com.dertefter.design.icons.Icons

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CrashReportsScreen(
    onEvent: (Event) -> Unit,
    uiState: UiState,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
            LargeFlexibleTopAppBar(
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
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.reports) { report ->
                        ListItem(
                            modifier = Modifier.clickable { onEvent(Event.OnClickReport(report.path)) },
                            headlineContent = {
                                Text(text = report.name)
                            },
                            supportingContent = {
                                Text(text = report.path)
                            },
                            trailingContent = {
                                IconButton(onClick = { onEvent(Event.OnDeleteReport(report.path)) }) {
                                    Icon(imageVector = Icons.Delete, contentDescription = "Удалить")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
