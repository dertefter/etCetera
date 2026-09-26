package com.dertefter.etcetera.navigation

import android.net.Uri
import com.dertefter.navigation.Routes

object DeepLinkParser {

    private val RESERVED_PATHS = setOf(
        "feed", "search", "notifications", "settings", "auth",
        "login", "register", "about", "privacy", "terms",
    )

    fun parse(uri: Uri?): Routes? {
        if (uri == null) return null

        val scheme = uri.scheme?.lowercase() ?: return null
        if (scheme != "http" && scheme != "https" && scheme != "etcetera") return null

        val host = uri.host?.lowercase()
        if (scheme == "http" || scheme == "https") {
            if (host != "xn--d1ah4a.com" && host != "итд.com") return null
        }

        val pathSegments = uri.pathSegments ?: return null
        if (pathSegments.isEmpty()) return null

        // Format: /@username/post/{postId} -> Routes.Post
        if (pathSegments.size >= 3 && pathSegments[0].startsWith("@") && pathSegments[1] == "post") {
            val postId = pathSegments[2]
            if (postId.isNotEmpty()) {
                return Routes.Post(postId = postId)
            }
        }

        // Format: /post/{postId} -> Routes.Post
        if (pathSegments.size >= 2 && pathSegments[0] == "post") {
            val postId = pathSegments[1]
            if (postId.isNotEmpty()) {
                return Routes.Post(postId = postId)
            }
        }

        // Format: /hashtag/{hashtagName} or /tag/{hashtagName} -> Routes.HashtagFeed
        if (pathSegments.size >= 2 && (pathSegments[0] == "hashtag" || pathSegments[0] == "tag")) {
            val hashtagName = pathSegments[1]
            if (hashtagName.isNotEmpty()) {
                return Routes.HashtagFeed(hashtagName = hashtagName)
            }
        }

        // Format: /@username -> Routes.UserByUsername
        val firstSegment = pathSegments[0]
        if (firstSegment.startsWith("@")) {
            val username = firstSegment.removePrefix("@")
            if (username.isNotEmpty()) {
                return Routes.UserByUsername(username = username)
            }
        }

        // for userId
        if (pathSegments.size == 1) {
            val userId = firstSegment.removePrefix("@")
            if (userId.isNotEmpty() && userId.lowercase() !in RESERVED_PATHS) {
                return Routes.User(userId = userId)
            }
        }

        return null
    }
}
