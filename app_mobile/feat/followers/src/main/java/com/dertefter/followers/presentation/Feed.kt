package com.dertefter.followers.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.theme.spacing
import com.dertefter.followers.R
import com.dertefter.followers.presentation.component.FollowerUserCard
import com.dertefter.followers.presentation.component.FollowerUserCardVertical
import com.jamal_aliev.paginator.compose.cursor.paginated
import com.jamal_aliev.paginator.compose.cursor.rememberPaginated
import com.jamal_aliev.paginator.core.extension.isErrorState
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Feed(
    paginator: MutableCursorPaginator<String, FollowerUserDto>,
    onEvent: (Event) -> Unit,
    uiState: PaginatorUiState<FollowerUserDto>,
    contentPadding: PaddingValues = PaddingValues(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    listState: LazyGridState = rememberLazyGridState(),
    isGrid: Boolean = true,
) {
    val paged = paginator.rememberPaginated(state = listState)

    val verticalSpace by animateDpAsState(
        if (isGrid) (MaterialTheme.spacing.extraLarge) else (MaterialTheme.spacing.small)
    )

    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        },
        label = "feed_state",
        modifier = Modifier
            .fillMaxSize()
    )
    { state ->
        when (state) {
            PaginatorUiState.Idle -> {
                Box(Modifier
                    .padding(contentPadding)
                    .fillMaxSize(), contentAlignment = Alignment.Center) {
                    AppLoadingIndicator()
                }
            }

            is PaginatorUiState.Loading -> {
                Box(Modifier
                    .padding(contentPadding)
                    .fillMaxSize(), contentAlignment = Alignment.Center) {
                    AppLoadingIndicator()
                }
            }

            is PaginatorUiState.Empty -> {
                Box(Modifier
                    .padding(contentPadding)
                    .fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Постов пока нет")
                }
            }

            is PaginatorUiState.Error -> {
                Box(Modifier
                    .padding(contentPadding)
                    .fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ошибка загрузки: ${state.state.exception.message}")
                }
            }

            is PaginatorUiState.Content -> {
                LazyVerticalGrid(
                    columns = if (isGrid) GridCells.Adaptive(140.dp) else GridCells.Fixed(1),
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (scrollBehavior != null) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                            else Modifier
                        ),
                    state = listState,
                    contentPadding = PaddingValues(
                        top = contentPadding.calculateTopPadding() + MaterialTheme.spacing.defaultScreenPadding,
                        bottom = contentPadding.calculateBottomPadding() + MaterialTheme.spacing.defaultScreenPadding,
                        start = contentPadding.calculateStartPadding(LocalLayoutDirection.current) + MaterialTheme.spacing.defaultScreenPadding,
                        end = contentPadding.calculateEndPadding(LocalLayoutDirection.current) + MaterialTheme.spacing.defaultScreenPadding
                    ),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
                    verticalArrangement = Arrangement.spacedBy(verticalSpace)
                ) {
                    paginated(paged) {
                        itemsIndexed(
                            state.items,
                            key = { _, user -> user.id }) { index, followerUser ->
                            Crossfade(
                                targetState = isGrid,
                                modifier = Modifier.animateItem()
                            ) { isGrid ->
                                if (isGrid){
                                    FollowerUserCardVertical(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        followerUser = followerUser,
                                        onClick = { onEvent(Event.OnOpenUser(followerUser.id)) },
                                        onFollow = { onEvent(Event.OnFollow(followerUser.id)) },
                                        onUnfollow = { onEvent(Event.OnUnfollow(followerUser.id)) }
                                    )
                                } else {
                                    FollowerUserCard(
                                        followerUser = followerUser,
                                        onClick = { onEvent(Event.OnOpenUser(followerUser.id)) },
                                        onFollow = { onEvent(Event.OnFollow(followerUser.id)) },
                                        onUnfollow = { onEvent(Event.OnUnfollow(followerUser.id)) },
                                        index = index
                                    )
                                }

                            }


                        }

                        appendIndicator {
                            state.appendState?.let { appendState ->
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (appendState.isProgressState()) {
                                        AppLoadingIndicator()
                                    } else if (appendState.isErrorState()) {
                                        Text(stringResource(R.string.followers_empty))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
