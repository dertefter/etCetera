package com.dertefter.settings_oss

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.navigation.Navigator
import com.dertefter.settings_oss.presentation.Event
import com.dertefter.settings_oss.presentation.UiState
import com.github.droibit.oss_licenses.parser.OssLicenseParser
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class OssViewModel(
    private val navigator: Navigator,
    private val application: Application,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadLicenses()
    }

    fun loadLicenses() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val parser = OssLicenseParser()
                val licenses = (parser.parse(application) + bundledLicenses(application))
                    .sortedBy { it.library }
                _uiState.value = UiState.Success(licenses)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load licenses")
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnNavigateBack -> {
                navigator.navigateUp()
            }
            is Event.OnRetry -> {
                loadLicenses()
            }
        }
    }

}
