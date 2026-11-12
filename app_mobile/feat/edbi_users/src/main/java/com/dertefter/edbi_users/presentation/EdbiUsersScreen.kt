package com.dertefter.edbi_users.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dertefter.data.dto.ebdi.EbdiUser
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.appbar.AppTopBarStyle
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.offset.MutablePaginator
import com.jamal_aliev.paginator.offset.dsl.mutablePaginator
import com.jamal_aliev.paginator.offset.load.LoadResult
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun EdbiUsersScreen(
    onEvent: (Event) -> Unit,
    uiState: PaginatorUiState<EbdiUser>,
    paginator: MutablePaginator<EbdiUser>?
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyGridState()
    var isGrid by rememberSaveable { mutableStateOf(false) }

    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = uiState is PaginatorUiState.Content && uiState.prependState.isProgressState()

    val hazeState = rememberHazeState()
    val uriHandler = LocalUriHandler.current

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { onEvent(Event.OnRefresh) },
        indicator = {
            PullToRefreshIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState,
                isRefreshing = isRefreshing
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    hazeState = hazeState,
                    appTopBarStyle = AppTopBarStyle.SMALL,
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(
                                        onClick = {
                                            uriHandler.openUri("https://itdsdk.qzz.io/ebdi/users")
                                        }
                                    )
                                    .padding(MaterialTheme.spacing.medium)
                            ) {
                                Text(
                                    text = "При поддержке",
                                    style = MaterialTheme.typography.titleSmallEmphasized
                                )

                                AsyncImage(
                                    model = "https://itdsdk.qzz.io/static/images/ebdi.png",
                                    modifier = Modifier.height(20.dp),
                                    contentDescription = "ЕДБИ"
                                )
                            }


                        }
                    },
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        AppNavigationIcon(
                            onClick = { onEvent(Event.OnBackClick) }
                        )
                    },
                    actions = {
                        AppNavigationIcon(
                            icon = if (isGrid) Icons.List else Icons.GridView,
                            onClick = { isGrid = !isGrid }
                        )
                    }
                )
            }
        ) { contentPadding ->
            Box(Modifier
                .hazeSource(
                    state = hazeState
                )
                .fillMaxSize()) {
                paginator?.let { p ->
                    Feed(
                        paginator = p,
                        onEvent = onEvent,
                        uiState = uiState,
                        contentPadding = contentPadding,
                        scrollBehavior = scrollBehavior,
                        listState = listState,
                        isGrid = isGrid
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EdbiUsersScreenPreview() {
    AppTheme {
        val sampleUsers = listOf(
            EbdiUser(
                id = "1",
                username = "johndoe",
                displayName = "John Doe",
                avatar = ""
            ),
            EbdiUser(
                id = "2",
                username = "janesmith",
                displayName = "Jane Smith",
                avatar = ""
            ),
            EbdiUser(
                id = "3",
                username = "janesmith",
                displayName = "Jane Smith",
                avatar = ""
            ),
            EbdiUser(
                id = "4",
                username = "janesmith",
                displayName = "Jane Smith",
                avatar = ""
            )
        )
        val samplePaginator = remember {
            mutablePaginator {
                load {
                    LoadResult(sampleUsers)
                }
            }
        }
        val sampleUiState = PaginatorUiState.Content(
            prependState = null,
            items = sampleUsers,
            appendState = null
        )
        EdbiUsersScreen(
            onEvent = {},
            uiState = sampleUiState,
            paginator = samplePaginator
        )
    }
}
