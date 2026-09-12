package com.dertefter.report.presentation

sealed interface Event {
    data object OnNavigateBack : Event
    data object OnSubmitReport : Event
    data class OnReasonSelected(val reason: String) : Event
    data class OnDescriptionChanged(val description: String) : Event
}
