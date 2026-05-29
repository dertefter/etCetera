package com.dertefter.notifications.presentation

sealed interface Event {

    data object OnRefresh : Event
    data class OnOpenUser(val userId: String) : Event

}
