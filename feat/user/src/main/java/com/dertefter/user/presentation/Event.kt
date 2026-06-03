package com.dertefter.user.presentation

sealed interface Event {
    data object OnLoadMore : Event
    data class OnRefresh(val tab: FeedTab) : Event
    data object OnNavigateBack : Event
    data class OnTabSelected(val tab: FeedTab) : Event
    data class OnLike(val postId: String) : Event
    data class OnUnlike(val postId: String) : Event
    data class OnNavigateToComments(val postId: String) : Event
    data class OnUpdateStats(val ids: List<String>) : Event
    data class OnShare(val userId: String) : Event
    data class OnBlock(val userId: String) : Event

    data class OnSaveBio(val bio: String) : Event

    data class OnOpenPost(val postId: String) : Event

    data class OnOpenUser(val userId: String) : Event

    data object OnOpenNewPost : Event

    data class OnOpenFollowers(val userId: String) : Event

    data class OnOpenFollowing(val userId: String) : Event


    data class OnFollow(val userId: String) : Event

    data object OnBannerEdit : Event

    data class OnUnfollow(val userId: String) : Event
}
