package com.dertefter.onboarding.presentation

sealed class Event {
    object OnNavigateUp : Event()

    object OnNextPage : Event()
}
