package com.dertefter.settings_about

import androidx.lifecycle.ViewModel
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.settings_about.presentation.Event
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey


@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class SettingsAboutViewModel(
    private val navigator: Navigator
) : ViewModel() {

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnNavigateBack -> {
                navigator.navigateUp()
            }

            is Event.OnOpenCrashlytics -> {
                navigator.navigate(Routes.CrashReports)
            }

        }
    }

}
