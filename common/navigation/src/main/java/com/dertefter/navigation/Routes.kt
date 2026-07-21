package com.dertefter.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes : NavKey {

    @Serializable
    data object Auth : Routes

    @Serializable
    data object Search : Routes


    @Serializable
    data object CrashReports : Routes

    @Serializable
    data object Feed : Routes

    @Serializable
    data class Notifications(
        val showBackButton: Boolean = true
    ) : Routes

    @Serializable
    data object BannerEdit : Routes

    @Serializable
    data class NewPost(
        val wallRecipientId: String? = null,
        val postIdForRepost: String? = null,
    ) : Routes

    @Serializable
    data class NewComment(val postId: String) : Routes

    @Serializable
    data class NewCommentReply(val postId: String, val commentId: String, val userId: String) : Routes

    @Serializable
    data class Comments(val postId: String) : Routes

    @Serializable
    data class Post(val postId: String) : Routes

    @Serializable
    data class AttachmentsViewer(val attachments: List<AttachmentNavigationModel>, val viewPosition: Int = 0) : Routes

    @Serializable
    data class User(
        val userId: String,
        val showBackButton: Boolean = true
    ) : Routes

    @Serializable
    data class Followers(
        val userId: String,
        val startTabIsFollowing: Boolean = false
    ) : Routes

    @Serializable
    data class HashtagFeed(val hashtagName: String) : Routes

}
