package com.dertefter.onboarding

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dertefter.onboarding.presentation.OnBoardingScreen

@Composable
fun OnBoardingRoute(
    viewModel: OnBoardingViewModel = hiltViewModel(),
) {

    OnBoardingScreen(
        onEvent = { event ->
            viewModel.onEvent(event)
        },
    )


}
