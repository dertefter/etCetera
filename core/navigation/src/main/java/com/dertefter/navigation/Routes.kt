package com.dertefter.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes {


    @Serializable
    data object Tab1 : Routes

    @Serializable
    data object Tab2 : Routes

    @Serializable
    data object Tab3 : Routes

    @Serializable
    data object Tab4 : Routes

    @Serializable
    data object Auth : Routes


    @Serializable
    data object Feed : Routes

    @Serializable
    data object Notifications : Routes

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
