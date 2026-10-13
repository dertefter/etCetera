package com.dertefter.data.repository

import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.event.CurrentEvent
import kotlinx.coroutines.flow.Flow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
class EventsRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val crashlyticsRepository: CrashlyticsRepository,
) : EventsRepository {

    override val currentEvent: Flow<CurrentEvent?> = localDataSource.currentEvent

    override suspend fun updateCurrentEvent(): Result<CurrentEvent?> {
        return remoteDataSource.getPortal()
            .onFailureLog(crashlyticsRepository)
            .onSuccess {
                localDataSource.saveCurrentEvent(it)
            }
    }
}
