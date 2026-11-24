package com.dertefter.messanger.presentation

sealed interface Event {

    data object OnRefresh : Event

    data object OnNavigateBack : Event

    data class OnOpenChat(val chatId: String) : Event

}
