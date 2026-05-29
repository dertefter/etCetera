package com.dertefter.notifications.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.dertefter.data.dto.notifications.NotificationDto
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.page.PaginatorUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotificationsScreen(
    onEvent: (Event) -> Unit,
    uiState: PaginatorUiState<NotificationDto>,
    paginator: MutableCursorPaginator<NotificationDto>,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            val containerColor = lerp(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surfaceContainer,
                scrollBehavior.state.overlappedFraction
            )
            Surface(color = containerColor) {
                Column {
                    LargeFlexibleTopAppBar(
                        title = {
                            Text("Уведомления")
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent
                        ),
                        scrollBehavior = scrollBehavior,
                    )
                }
            }
        }
    ) { padding ->
        NotificationsFeed(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = padding,
            paginator = paginator,
            uiState = uiState,
            onEvent = onEvent,
            scrollBehavior = scrollBehavior
        )
    }
}
