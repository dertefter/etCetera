package com.dertefter.onboarding

import androidx.lifecycle.ViewModel
import com.dertefter.data.repository.SettingsRepository
import com.dertefter.onboarding.presentation.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {




    fun onEvent(event: Event) {
        when (event) {

            else -> {}

        }
    }
}
