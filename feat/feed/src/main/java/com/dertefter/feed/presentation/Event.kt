package com.dertefter.feed.presentation

import com.dertefter.design.components.post.AttachmentUiModel

sealed interface Event {
    data object OnLoadMore : Event
    data class OnRefresh(val tab: FeedTab) : Event
    data object OnOpenNotifications : Event
    data class OnTabSelected(val tab: FeedTab) : Event

    data class OnLike(val postId: String) : Event

    data class OnUnlike(val postId: String) : Event

    data class OnOpenUser(val userId: String?) : Event

    data object OnOpenNewPost : Event

    data object OnOpenSearch: Event

    data class OnNavigateToComments(val postId: String) : Event

    data class OnOpenPost(val postId: String) : Event

    data class OnOpenAttachmentsViewer(val attachments: List<AttachmentUiModel>, val position: Int = 0) : Event

    data class OnVote(val postId: String, val optionIds: List<String>) : Event

    data class OnUpdateStats(val ids: List<String>) : Event
}
