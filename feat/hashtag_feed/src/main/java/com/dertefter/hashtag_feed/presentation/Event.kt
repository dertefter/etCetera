package com.dertefter.hashtag_feed.presentation

import com.dertefter.design.components.post.AttachmentUiModel

sealed interface Event {
    data object OnLoadMore : Event
    data object OnRefresh : Event
    data object OnNavigateBack : Event
    data class OnLike(val postId: String) : Event
    data class OnUnlike(val postId: String) : Event
    data class OnNavigateToComments(val postId: String) : Event
    data class OnUpdateStats(val ids: List<String>) : Event

    data class OnOpenPost(val postId: String) : Event

    data class OnOpenUser(val userId: String) : Event

    data class  OnOpenAttachmentsViewer(val attachments: List<AttachmentUiModel>, val position: Int = 0)  : Event
}
