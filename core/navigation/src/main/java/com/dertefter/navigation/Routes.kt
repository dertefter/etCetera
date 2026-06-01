package com.dertefter.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes {

    @Serializable
    data object Auth : Routes


    @Serializable
    data object CrashReports : Routes

    @Serializable
    data object Feed : Routes

    @Serializable
    data object Notifications : Routes

    @Serializable
    data object BannerEdit : Routes

    @Serializable
    data class NewPost(val wallRecipientId: String?) : Routes

    @Serializable
    data class Comments(val postId: String) : Routes

    @Serializable
    data class User(val userId: String?) : Routes

    @Serializable
    data class Followers(
        val userId: String,
        val startTabIsFollowing: Boolean = false
    ) : Routes

}
