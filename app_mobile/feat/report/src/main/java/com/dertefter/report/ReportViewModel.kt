package com.dertefter.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.repository.ReportsRepository
import com.dertefter.navigation.Navigator
import com.dertefter.report.presentation.Event
import com.dertefter.report.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportsRepository: ReportsRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun initWith(targetType: String, targetId: String) {
        _uiState.update { it.copy(targetType = targetType, targetId = targetId) }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnNavigateBack -> {
                navigator.hideBottomSheet()
            }
            is Event.OnReasonSelected -> {
                _uiState.update { it.copy(reason = event.reason) }
            }
            is Event.OnDescriptionChanged -> {
                _uiState.update { it.copy(description = event.description) }
            }
            is Event.OnSubmitReport -> {
                submitReport()
            }
        }
    }

    private fun submitReport() {
        val state = _uiState.value
        val targetType = state.targetType ?: return
        val targetId = state.targetId ?: return
        val reason = state.reason ?: return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            reportsRepository.createReport(
                targetType = targetType,
                targetId = targetId,
                reason = reason,
                description = state.description.ifBlank { null }
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }
}
