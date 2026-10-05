package com.dertefter.banner_edit.usecase

import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.dto.me.UpdateMeResponse
import com.dertefter.data.repository.MeRepository
import javax.inject.Inject

class SaveBannerUseCase @Inject constructor(
    private val meRepository: MeRepository
) {
    suspend operator fun invoke(bannerId: String?): Result<UpdateMeResponse> {
        return meRepository.saveMe(
            UpdateMeRequestDto(
                bannerId = bannerId,
            )
        )
    }
}
