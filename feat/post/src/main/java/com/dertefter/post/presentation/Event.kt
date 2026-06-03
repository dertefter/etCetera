package com.dertefter.post.presentation

sealed interface Event {

    data object OnRefresh : Event
    data class OnOpenUser(val userId: String?) : Event
    data object OnLike : Event

    data object OnNavigateBack : Event

    data object OnUnlike : Event

    data class OnOpenPost(val postId: String) : Event
    data class OnVote(val optionIds: List<String>) : Event

}
