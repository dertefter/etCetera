package com.dertefter.settings_security.presentation

sealed interface Event {

    data object OnNavigateBack : Event
    data object OnRefresh : Event

    data class OnDeleteSession(val sessionId: String) : Event

    data object OnDeleteAllSessions : Event


}
