package com.dertefter.data.repository

import com.dertefter.data.dto.me.MeDto
import com.dertefter.data.dto.me.PrivacyDto
import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.dto.me.UpdateMeResponseDto
import com.dertefter.data.dto.me.UpdatePrivacyRequestDto
import kotlinx.coroutines.flow.Flow

interface MeRepository {

    val me: Flow<MeDto?>

    suspend fun updateMe(): Result<MeDto>

    suspend fun saveMe(updateMeRequestDto: UpdateMeRequestDto): Result<UpdateMeResponseDto>

    val privacy: Flow<PrivacyDto?>

    suspend fun updatePrivacy(): Result<PrivacyDto>

    suspend fun savePrivacy(updatePrivacyRequestDto: UpdatePrivacyRequestDto): Result<PrivacyDto>


    /*

GET https://xn--d1ah4a.com/api/users/me/privacy
Пример ответа:
{
    "isPrivate": false,
    "wallAccess": "everyone",
    "likesVisibility": "nobody",
    "messageAccess": "everyone",
    "showLastSeen": true
}

============

PUT https://xn--d1ah4a.com/api/users/me/privacy
{"wallAccess":"everyone","likesVisibility":"nobody","showLastSeen":true}
Пример ответа:
{
    "isPrivate": false,
    "wallAccess": "everyone",
    "likesVisibility": "nobody",
    "messageAccess": "everyone",
    "showLastSeen": true
}

     */


}