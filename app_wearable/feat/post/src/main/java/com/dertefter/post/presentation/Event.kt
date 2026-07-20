package com.dertefter.post.presentation

import com.dertefter.design.components.post.AttachmentUiModel

sealed interface Event {

    data object OnRefresh : Event

    data class OnRepost(val postId: String) : Event
    data class OnOpenUser(val userId: String) : Event
    data object OnLike : Event

    data class OnPin(val postId: String) : Event

    data class OnUnpin(val postId: String): Event

    data class OnDeletePost(val postId: String) : Event

    data class OnOpenHashtag(val name: String) : Event

    data object OnNavigateBack : Event

    data object OnUnlike : Event

    data class OnOpenPost(val postId: String) : Event
    data class OnVote(val optionIds: List<String>) : Event

    data class  OnOpenAttachmentsViewer(val attachments: List<AttachmentUiModel>, val position: Int = 0)  : Event

}
