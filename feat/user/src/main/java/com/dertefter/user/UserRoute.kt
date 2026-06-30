package com.dertefter.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.user.presentation.FeedTab
import com.dertefter.user.presentation.UserScreen

@Composable
fun UserRoute(
    userId: String? = null,
    viewModel: UserViewModel = hiltViewModel(),
) {

    val userUiState by viewModel.userUiState.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    val postsUiState by viewModel.uiStates[FeedTab.POSTS]!!.collectAsStateWithLifecycle()
    val likesUiState by viewModel.uiStates[FeedTab.LIKES]!!.collectAsStateWithLifecycle()

    val uiStates = mapOf(
        FeedTab.POSTS to postsUiState,
        FeedTab.LIKES to likesUiState
    )

    val paginators by viewModel.paginators.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initWithUserId(userId)
    }

    UserScreen(
        userUiState = userUiState,
        selectedTab = selectedTab,
        uiStates = uiStates,
        paginators = paginators,
        onEvent = viewModel::onEvent
    )


}
