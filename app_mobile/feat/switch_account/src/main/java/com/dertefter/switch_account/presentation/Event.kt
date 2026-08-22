package com.dertefter.switch_account.presentation

sealed interface Event {
    data class OnSwitchAccount(val login: String) : Event
    data object OnAddAccount : Event
    data class OnRemoveAccountFromHistory(val login: String) : Event
    data object OnNavigateBack : Event
}
