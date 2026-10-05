package com.dertefter.user.usecase

import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.dto.me.UpdateMeResponse
import com.dertefter.data.repository.MeRepository
import javax.inject.Inject

class SaveMeUseCase @Inject constructor(
    private val meRepository: MeRepository
) {
    suspend operator fun invoke(updateMeRequestDto: UpdateMeRequestDto): Result<UpdateMeResponse> {
        return meRepository.saveMe(updateMeRequestDto)
    }
}
