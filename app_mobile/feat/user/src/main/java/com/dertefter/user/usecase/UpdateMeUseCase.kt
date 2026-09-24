package com.dertefter.user.usecase

import com.dertefter.data.dto.me.MeDto
import com.dertefter.data.repository.MeRepository
import javax.inject.Inject

class UpdateMeUseCase @Inject constructor(
    private val meRepository: MeRepository
) {
    suspend operator fun invoke(): Result<MeDto> {
        return meRepository.updateMe()
    }
}
