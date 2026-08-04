package com.dertefter.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.search.SearchHashtagDto
import com.dertefter.data.dto.search.SearchUserDto
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.search.R
import com.dertefter.search.presentation.component.SearchHashtagCard
import com.dertefter.search.presentation.component.SearchUserCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onEvent: (Event) -> Unit,
    uiState: UiState,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val textFieldState = rememberTextFieldState()

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
                    LargeFlexibleTopAppBar(
                        title = {
                            Text(text = stringResource(R.string.search_title))
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent
                        ),
                        scrollBehavior = scrollBehavior,
                    )

                    TextField(
                        state = textFieldState,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp)
                            .fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search_hint)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(com.dertefter.design.R.drawable.ic_search),
                                contentDescription = null
                            )
                        },
                        shape = CircleShape,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        )
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
                    onClick = {
                        onEvent(
                            Event.OnOpenHashtag(hashtag.name)
                        )
                    }
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

