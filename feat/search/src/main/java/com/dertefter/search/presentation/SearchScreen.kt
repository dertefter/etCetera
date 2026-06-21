package com.dertefter.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.search.SearchHashtagDto
import com.dertefter.data.dto.search.SearchUserDto
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.search.R
import com.dertefter.search.presentation.component.SearchHashtagCard
import com.dertefter.search.presentation.component.SearchUserCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(
    onEvent: (Event) -> Unit,
    uiState: UiState,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(textFieldState.text) {
        onEvent(Event.OnSearchQueryChanged(textFieldState.text.toString()))
    }

    Scaffold(
        topBar = {
            val containerColor = lerp(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surfaceContainer,
                scrollBehavior.state.overlappedFraction
            )
            Surface(color = containerColor) {
                Column {
                    TopAppBar(
                        navigationIcon = {
                            AppNavigationIcon(
                                contentDescription = stringResource(R.string.navigate_back),
                                onClick = {
                                    onEvent(Event.OnNavigateBack)
                                }
                            )
                        },
                        title = {},
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent
                        ),
                        scrollBehavior = scrollBehavior,
                    )

                    SearchBar(
                        state = searchBarState,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        inputField = {
                            SearchBarDefaults.InputField(
                                modifier = Modifier
                                    .padding(
                                        horizontal = MaterialTheme.spacing.small,
                                        vertical = MaterialTheme.spacing.extraSmall
                                    ),
                                textFieldState = textFieldState,
                                searchBarState = searchBarState,
                                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                                placeholder = {
                                    Text(
                                        modifier = Modifier.clearAndSetSemantics {},
                                        text = stringResource(R.string.search_title)
                                    )
                                },
                            )
                        }
                    )
                }
            }
        }
    ) { contentPadding ->

        LazyColumn(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            contentPadding = contentPadding
        ) {
            item{}
            items(uiState.users) { user ->
                SearchUserCard(
                    searchUser = user,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                    onClick = {
                        onEvent(Event.OnOpenUser(user.id))
                    }
                )
            }
            items(uiState.hashtags) { hashtag ->
                SearchHashtagCard(
                    hashtag = hashtag,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                    onClick = {}
                )
            }
            item{}
        }

    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    AppTheme {
        SearchScreen(
            onEvent = {},
            uiState = UiState(
                query = "test",
                users = listOf(
                    SearchUserDto(
                        id = "1",
                        username = "johndoe",
                        displayName = "John Doe",
                        avatar = "",
                        verified = true,
                        hasNuksta = false,
                        followersCount = 100
                    )
                ),
                hashtags = listOf(
                    SearchHashtagDto(
                        id = "12",
                        name = "android",
                        postsCount = 50
                    ),
                    SearchHashtagDto(
                        id = "122",
                        name = "android",
                        postsCount = 50
                    )
                )
            )
        )
    }
}

