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
    val postsUiState by viewModel.uiStates[FeedTab.POSTS]!!.collectAsStateWithLifecycle()
    val paginators by viewModel.paginators.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initWithUserId(userId)
    }

    val postsPaginator = paginators[FeedTab.POSTS]

    if (postsPaginator != null){
        UserScreen(
            userUiState = userUiState,
            uiState = postsUiState,
            paginator = postsPaginator,
            onEvent = viewModel::onEvent
        )
    }


}
