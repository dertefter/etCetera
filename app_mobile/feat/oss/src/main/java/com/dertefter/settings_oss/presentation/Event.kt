package com.dertefter.settings_oss.presentation

sealed interface Event {

    data object OnNavigateBack : Event

    data object OnRetry : Event

}
