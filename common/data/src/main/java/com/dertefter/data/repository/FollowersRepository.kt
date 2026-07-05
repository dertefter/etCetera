package com.dertefter.data.repository

import com.dertefter.data.dto.followers.FollowerUserDto
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator

interface FollowersRepository {

    fun getFollowersPaginator(userId: String): MutableCursorPaginator<String, FollowerUserDto>

    fun getFollowingPaginator(userId: String): MutableCursorPaginator<String, FollowerUserDto>

    suspend fun updateFollowingStatus(userId: String, isFollowing: Boolean)
}
