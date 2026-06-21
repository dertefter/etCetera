package com.dertefter.search.presentation

sealed interface Event {

    data object OnNavigateBack : Event

    data class OnSearchQueryChanged(val q: String) : Event

    data class OnOpenUser(val userId: String) : Event


}
