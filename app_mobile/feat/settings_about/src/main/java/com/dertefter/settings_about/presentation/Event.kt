package com.dertefter.settings_about.presentation

sealed interface Event {

    data object OnNavigateBack : Event

}
