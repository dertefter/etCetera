package com.dertefter.settings.presentation

import com.dertefter.navigation.Routes

sealed interface Event {

    data object OnNavigateBack : Event
    data class OnNavigateTo(val route: Routes) : Event

}
