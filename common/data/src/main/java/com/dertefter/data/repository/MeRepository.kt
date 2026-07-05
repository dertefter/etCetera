package com.dertefter.data.repository

import com.dertefter.data.dto.me.MeDto
import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.dto.me.UpdateMeResponseDto
import kotlinx.coroutines.flow.Flow

interface MeRepository {

    val meDto: Flow<MeDto?>

    suspend fun fetchMe(): Result<MeDto>

    suspend fun updateMe(updateMeRequestDto: UpdateMeRequestDto): Result<UpdateMeResponseDto>

}