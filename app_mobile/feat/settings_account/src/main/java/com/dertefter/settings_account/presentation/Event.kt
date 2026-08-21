package com.dertefter.settings_account.presentation

sealed interface Event {

    data object OnNavigateBack : Event
    data object OnRefresh : Event
    data object OnSave : Event

    data class OnDisplayNameChange(val value: String) : Event
    data class OnUsernameChange(val value: String) : Event
    data class OnBioChange(val value: String) : Event

}
