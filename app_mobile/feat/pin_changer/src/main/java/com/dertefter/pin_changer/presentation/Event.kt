package com.dertefter.pin_changer.presentation

sealed interface Event {

    data object OnNavigateBack : Event
    data object OnRefresh : Event
    data class OnPinChange(val slug: String?) : Event

}
