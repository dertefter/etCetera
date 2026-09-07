package com.dertefter.settings_about

import androidx.lifecycle.ViewModel
import com.dertefter.navigation.Navigator
import com.dertefter.settings_about.presentation.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsAboutViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel() {

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnNavigateBack -> {
                navigator.navigateUp()
            }

        }
    }

}
