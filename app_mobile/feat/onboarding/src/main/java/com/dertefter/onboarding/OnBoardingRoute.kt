package com.dertefter.onboarding

import androidx.compose.runtime.Composable
import dev.zacsweers.metrox.viewmodel.metroViewModel
import com.dertefter.onboarding.presentation.OnBoardingScreen

@Composable
fun OnBoardingRoute(
    viewModel: OnBoardingViewModel = metroViewModel(),
) {

    OnBoardingScreen(
        onEvent = { event ->
            viewModel.onEvent(event)
        },
    )


}
