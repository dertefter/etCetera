package com.dertefter.data.repository

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.user.FollowResponseDto
import com.dertefter.data.dto.user.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val followersRepository: FollowersRepository
) : UserRepository {

    override fun getUser(userId: String): Flow<UserDto?> {
        return localDataSource.getUser(userId)
    }

    override suspend fun updateUser(userId: String): Result<UserDto> {
        return remoteDataSource.getUser(userId).onSuccess {
            localDataSource.saveUser(it)
        }
    }

    override suspend fun follow(userId: String): Result<FollowResponseDto> {
        applyOptimisticFollow(userId, true)
        return remoteDataSource.follow(userId).onSuccess { response ->
            handleFollowResponse(userId, response)
        }.onFailure {
            applyOptimisticFollow(userId, false)
        }
    }

    override suspend fun unfollow(userId: String): Result<FollowResponseDto> {
        applyOptimisticFollow(userId, false)
        return remoteDataSource.unfollow(userId).onSuccess { response ->
            handleFollowResponse(userId, response)
        }.onFailure {
            applyOptimisticFollow(userId, true)
        }
    }

    private suspend fun applyOptimisticFollow(userId: String, following: Boolean) {
        followersRepository.updateFollowingStatus(userId, following)
        localDataSource.getUser(userId).firstOrNull()?.let { user ->
            if (user.isFollowing != following) {
                val newCount = if (following) user.followersCount + 1 else (user.followersCount - 1).coerceAtLeast(0)
                localDataSource.saveUser(user.copy(isFollowing = following, followersCount = newCount))
            }
        }
    }

    private suspend fun handleFollowResponse(userId: String, response: FollowResponseDto) {
        followersRepository.updateFollowingStatus(userId, response.following)
        localDataSource.getUser(userId).firstOrNull()?.let { user ->
            localDataSource.saveUser(
                user.copy(
                    isFollowing = response.following,
                    followersCount = response.followersCount
                )
            )
        }
    }
}
