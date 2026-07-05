package com.dertefter.notifications.presentation

sealed interface Event {

    data object OnRefresh : Event

    data object OnNavigateBack : Event

    data class OnOpenUser(val userId: String) : Event

    data class OnOpenPost(val postId: String) : Event

    data class OnFilterChanged(val type: String?) : Event


}
