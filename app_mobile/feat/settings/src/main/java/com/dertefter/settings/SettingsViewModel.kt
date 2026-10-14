package com.dertefter.settings

import androidx.lifecycle.ViewModel
import com.dertefter.navigation.Navigator
import com.dertefter.settings.presentation.Event
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey


@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class SettingsViewModel(
    private val navigator: Navigator
) : ViewModel() {

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnNavigateBack -> {
                navigator.navigateUp()
            }

            is Event.OnNavigateTo -> {
                navigator.navigate(
                    event.route
                )
            }
        }
    }
}
