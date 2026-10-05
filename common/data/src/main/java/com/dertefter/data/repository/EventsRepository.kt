package com.dertefter.data.repository

import com.dertefter.data.dto.event.CurrentEvent
import kotlinx.coroutines.flow.Flow

interface EventsRepository {

    val currentEvent: Flow<CurrentEvent?>

    suspend fun updateCurrentEvent(): Result<CurrentEvent?>

}
