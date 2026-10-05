package com.dertefter.data.repository

import com.dertefter.data.dto.feed.Pin
import com.dertefter.data.dto.me.Me
import com.dertefter.data.dto.me.Privacy
import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.dto.me.UpdateMeResponse
import com.dertefter.data.dto.me.UpdatePrivacyRequestDto
import kotlinx.coroutines.flow.Flow

interface MeRepository {

    val me: Flow<Me?>

    suspend fun updateMe(): Result<Me>

    suspend fun saveMe(updateMeRequestDto: UpdateMeRequestDto): Result<UpdateMeResponse>

    val privacy: Flow<Privacy?>

    suspend fun updatePrivacy(): Result<Privacy>

    suspend fun savePrivacy(updatePrivacyRequestDto: UpdatePrivacyRequestDto): Result<Privacy>

    val pins: Flow<List<Pin>?>

    suspend fun updatePins(): Result<List<Pin>>

    suspend fun savePin(slug: String): Result<Unit>

    suspend fun deletePin(): Result<Unit>

}
