package com.dertefter.data.repository

import com.dertefter.data.dto.feed.PostDto
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator

interface FeedRepository {

    fun getFeedPaginator(tab: String): MutableCursorPaginator<String, PostDto>

    fun getHashtagPaginator(hashtag: String): MutableCursorPaginator<String, PostDto>

    fun getPostsPaginator(userId: String, sort: String = "new", pinnedPostId: () -> String?): MutableCursorPaginator<String, PostDto>

    fun getLikedPostsPaginator(userId: String): MutableCursorPaginator<String, PostDto>
}
