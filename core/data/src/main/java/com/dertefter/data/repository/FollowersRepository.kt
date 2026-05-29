package com.dertefter.data.repository

import com.dertefter.data.dto.followers.FollowerUserDto
import com.jamal_aliev.paginator.MutableCursorPaginator

interface FollowersRepository {


    fun getFollowersPaginator(userId: String): MutableCursorPaginator<FollowerUserDto>

    fun getFollowingPaginator(userId: String): MutableCursorPaginator<FollowerUserDto>

    suspend fun updateFollowingStatus(userId: String, isFollowing: Boolean)
}
