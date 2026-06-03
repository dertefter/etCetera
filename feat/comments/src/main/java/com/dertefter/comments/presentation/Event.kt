package com.dertefter.comments.presentation

sealed interface Event {
    data class OnTabSelected(val tab: CommentSort) : Event
    data object OnLoadMore : Event
    data class OnRefresh(val tab: CommentSort, val postId: String) : Event

    data class OnLike(val commentId: String) : Event

    data class OnUnlike(val commentId: String) : Event

    data class OnLoadMoreReplies(val commentId: String) : Event

    data class OnOpenUser(val userId: String?) : Event

}
