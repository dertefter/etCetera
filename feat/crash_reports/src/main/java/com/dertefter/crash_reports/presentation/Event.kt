package com.dertefter.crash_reports.presentation

sealed interface Event {

    data object OnRefresh : Event
    data class OnClickReport(val path: String) : Event
    data class OnDeleteReport(val path: String) : Event
    data class OnShareReport(val path: String) : Event
    data object OnDismissDialog : Event
    data object OnBack : Event

}
