package com.dertefter.hashtag_feed.presentation

import com.dertefter.design.components.post.AttachmentUiModel

sealed interface Event {
    data object OnLoadMore : Event
    data object OnRefresh : Event

    data class OnRepost(val postId: String) : Event

    data class OnPin(val postId: String) : Event

    data class OnVote(val postId: String, val optionIds: List<String>) : Event

    data class OnUnpin(val postId: String): Event

    data class OnDeletePost(val postId: String) : Event
    data object OnNavigateBack : Event
    data class OnLike(val postId: String) : Event

    data class OnOpenHashtag(val name: String) : Event
    data class OnUnlike(val postId: String) : Event
    data class OnNavigateToComments(val postId: String) : Event
    data class OnUpdateStats(val ids: List<String>) : Event

    data class OnOpenPost(val postId: String) : Event

    data class OnOpenUser(val userId: String) : Event

    data class  OnOpenAttachmentsViewer(val attachments: List<AttachmentUiModel>, val position: Int = 0)  : Event
}
