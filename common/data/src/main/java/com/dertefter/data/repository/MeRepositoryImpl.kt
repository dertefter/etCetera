package com.dertefter.data.repository

import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.me.MeDto
import com.dertefter.data.dto.me.PrivacyDto
import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.dto.me.UpdateMeResponseDto
import com.dertefter.data.dto.me.UpdatePrivacyRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class MeRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val crashlyticsRepository: CrashlyticsRepository,
) : MeRepository {

    override val me: Flow<MeDto?> = localDataSource.meDto

    override suspend fun updateMe(): Result<MeDto> {
        return remoteDataSource.getMe().onFailureLog(crashlyticsRepository).onSuccess {
            localDataSource.saveMe(it)
        }
    }

    override suspend fun saveMe(updateMeRequestDto: UpdateMeRequestDto): Result<UpdateMeResponseDto> {
        return remoteDataSource.updateMe(updateMeRequestDto).onFailureLog(crashlyticsRepository).onSuccess { response ->
            me.firstOrNull()?.let { currentMe ->
                localDataSource.saveMe(
                    currentMe.copy(
                        username = response.username,
                        displayName = response.displayName,
                        bio = response.bio
                    )
                )
            }
        }
    }

    override val privacy: Flow<PrivacyDto?> = localDataSource.privacy

    override suspend fun updatePrivacy(): Result<PrivacyDto> {
        return remoteDataSource.getPrivacy().onFailureLog(crashlyticsRepository).onSuccess {
            localDataSource.savePrivacy(it)
        }
    }

    override suspend fun savePrivacy(updatePrivacyRequestDto: UpdatePrivacyRequestDto): Result<PrivacyDto> {
        return remoteDataSource.updatePrivacy(updatePrivacyRequestDto).onFailureLog(crashlyticsRepository).onSuccess {
            localDataSource.savePrivacy(it)
        }
    }

}
