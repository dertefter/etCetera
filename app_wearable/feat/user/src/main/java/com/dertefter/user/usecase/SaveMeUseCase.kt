package com.dertefter.user.usecase

import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.dto.me.UpdateMeResponseDto
import com.dertefter.data.repository.MeRepository
import javax.inject.Inject

class SaveMeUseCase @Inject constructor(
    private val meRepository: MeRepository
) {
    suspend operator fun invoke(updateMeRequestDto: UpdateMeRequestDto): Result<UpdateMeResponseDto> {
        return meRepository.saveMe(updateMeRequestDto)
    }
}
