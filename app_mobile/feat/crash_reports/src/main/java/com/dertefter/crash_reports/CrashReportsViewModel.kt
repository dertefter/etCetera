package com.dertefter.crash_reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.crash_reports.presentation.Event
import com.dertefter.crash_reports.presentation.UiState
import com.dertefter.data.repository.CrashlyticsRepository
import com.dertefter.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CrashReportsViewModel @Inject constructor(
    private val crashReportsRepository: CrashlyticsRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnRefresh -> {
                refresh()
            }
            is Event.OnClickReport -> {
                _uiState.update { it.copy(selectedReportContent = crashReportsRepository.readCrashLog(event.path)) }
            }
            is Event.OnDeleteReport -> {
                deleteReport(event.path)
            }
            is Event.OnShareReport -> {
                // Handled in Route
            }
            is Event.OnDismissDialog -> {
                _uiState.update { it.copy(selectedReportContent = null) }
            }
            is Event.OnBack -> {
                navigator.navigateUp()
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val reports = crashReportsRepository.getCrashLogsList()
            _uiState.update { it.copy(reports = reports, isLoading = false) }
        }
    }

    private fun deleteReport(path: String) {
        viewModelScope.launch {
            crashReportsRepository.deleteCrashLog(path)
            refresh()
        }
    }
}
