package com.dertefter.data.repository

import com.dertefter.data.dto.user.BlockResponseDto
import com.dertefter.data.dto.user.FollowResponseDto
import com.dertefter.data.dto.user.UserDto
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUser(userId: String): Flow<UserDto?>

    suspend fun updateUser(userId: String): Result<UserDto>

    suspend fun follow(userId: String): Result<FollowResponseDto>

    suspend fun unfollow(userId: String): Result<FollowResponseDto>

    suspend fun block(userId: String): Result<BlockResponseDto>

    suspend fun unblock(userId: String): Result<BlockResponseDto>



}
