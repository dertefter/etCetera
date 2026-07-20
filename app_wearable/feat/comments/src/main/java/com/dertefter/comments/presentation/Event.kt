package com.dertefter.comments.presentation

sealed interface Event {
    data object OnLoadMore : Event
    data class OnRefresh(val postId: String) : Event

    data class OnLike(val commentId: String) : Event

    data class OnUnlike(val commentId: String) : Event

    data class OnLoadMoreReplies(val commentId: String) : Event

    data class OnOpenUser(val userId: String) : Event

    data class OnDeleteComment(val commentId: String) : Event

}
