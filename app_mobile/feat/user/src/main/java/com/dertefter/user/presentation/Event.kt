package com.dertefter.user.presentation

import com.dertefter.design.components.post.AttachmentUiModel

sealed interface Event {
    data object OnLoadMore : Event
    data class OnRefresh(val tab: FeedTab) : Event

    data class OnRepost(val postId: String) : Event

    data class OnEditPost(val postId: String) : Event

    data object OnNavigateBack : Event

    data object OnNavigateToAuth : Event
    data class OnSwitchAccount(val login: String) : Event
    data object OnAddAccount : Event
    data class OnRemoveAccountFromHistory(val login: String) : Event
    data class OnTabSelected(val tab: FeedTab) : Event
    data class OnLike(val postId: String) : Event
    data class OnUnlike(val postId: String) : Event
    data class OnNavigateToComments(val postId: String) : Event
    data class OnUpdateStats(val ids: List<String>) : Event
    data class OnVote(val postId: String, val optionIds: List<String>) : Event

    data class OnDeletePost(val postId: String) : Event
    data class OnShare(val userId: String) : Event
    data class OnBlock(val userId: String) : Event

    data class OnSaveBio(val bio: String) : Event

    data class OnOpenPost(val postId: String) : Event

    data class OnOpenUser(val userId: String) : Event

    data object OnOpenNewPost : Event

    data class OnOpenFollowers(val userId: String) : Event

    data class OnOpenFollowing(val userId: String) : Event


    data class OnFollow(val userId: String) : Event

    data class OnPin(val postId: String) : Event

    data class OnUnpin(val postId: String) : Event

    data object OnBannerEdit : Event

    data class OnOpenHashtag(val name: String) : Event

    data class OnUnfollow(val userId: String) : Event
    data class  OnOpenAttachmentsViewer(val attachments: List<AttachmentUiModel>, val position: Int = 0)  : Event
}
