package com.dertefter.data.repository

import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.feed.Pin
import com.dertefter.data.dto.me.Me
import com.dertefter.data.dto.me.Privacy
import com.dertefter.data.dto.me.SavePinRequestDto
import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.dto.me.UpdateMeResponse
import com.dertefter.data.dto.me.UpdatePrivacyRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class MeRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val crashlyticsRepository: CrashlyticsRepository,
) : MeRepository {

    override val me: Flow<Me?> = localDataSource.me

    override suspend fun updateMe(): Result<Me> {
        return remoteDataSource.getMe().onFailureLog(crashlyticsRepository).onSuccess {
            localDataSource.saveMe(it)
        }
    }

    override suspend fun saveMe(updateMeRequestDto: UpdateMeRequestDto): Result<UpdateMeResponse> {
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

    override val privacy: Flow<Privacy?> = localDataSource.privacy

    override suspend fun updatePrivacy(): Result<Privacy> {
        return remoteDataSource.getPrivacy().onFailureLog(crashlyticsRepository).onSuccess {
            localDataSource.savePrivacy(it)
        }
    }

    override suspend fun savePrivacy(updatePrivacyRequestDto: UpdatePrivacyRequestDto): Result<Privacy> {
        return remoteDataSource.updatePrivacy(updatePrivacyRequestDto).onFailureLog(crashlyticsRepository).onSuccess {
            localDataSource.savePrivacy(it)
        }
    }

    override val pins: Flow<List<Pin>?> = localDataSource.pins

    override suspend fun updatePins(): Result<List<Pin>> {
        return remoteDataSource.getPins().onFailureLog(crashlyticsRepository).onSuccess { pinsData ->
            localDataSource.savePins(pinsData.pins)
        }.map { it.pins }
    }

    override suspend fun savePin(slug: String): Result<Unit> {
        return remoteDataSource.savePin(SavePinRequestDto(slug)).onFailureLog(crashlyticsRepository).onSuccess {
            updatePins()
            updateMe()
        }
    }

    override suspend fun deletePin(): Result<Unit> {
        return remoteDataSource.deletePin().onFailureLog(crashlyticsRepository).onSuccess {
            updatePins()
            updateMe()
        }
    }

}
