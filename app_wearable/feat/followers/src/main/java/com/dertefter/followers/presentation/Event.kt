package com.dertefter.followers.presentation

sealed interface Event {
    data class OnTabSelected(val tab: Tab) : Event
    data class OnRefresh(val tab: Tab) : Event
    data class OnOpenUser(val userId: String) : Event
    data class OnFollow(val userId: String) : Event
    data class OnUnfollow(val userId: String) : Event
    data object OnBackClick : Event
}
