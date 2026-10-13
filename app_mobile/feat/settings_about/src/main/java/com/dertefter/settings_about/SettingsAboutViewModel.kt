package com.dertefter.settings_about

import androidx.lifecycle.ViewModel
import com.dertefter.navigation.Navigator
import com.dertefter.settings_about.presentation.Event
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey


@ViewModelKey(SettingsAboutViewModel::class)
@ContributesIntoMap(AppScope::class)
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
