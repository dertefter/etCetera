package com.dertefter.edbi_users.presentation

sealed interface Event {
    data object OnRefresh : Event
    data class OnOpenUser(val userId: String) : Event
    data object OnBackClick : Event
}
