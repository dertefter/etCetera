package com.dertefter.settings_about.presentation

import com.dertefter.navigation.Routes

sealed interface Event {

    data class OnNavigateTo(val route: Routes) : Event

    data object OnNavigateBack : Event

}
