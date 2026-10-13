package com.dertefter.onboarding

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.AppScope
import com.dertefter.data.repository.SettingsRepository
import com.dertefter.onboarding.presentation.Event
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@ViewModelKey(OnBoardingViewModel::class)
@ContributesIntoMap(AppScope::class)
class OnBoardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    fun onEvent(event: Event) {
        when (event) {
            else -> {}
        }
    }
}
