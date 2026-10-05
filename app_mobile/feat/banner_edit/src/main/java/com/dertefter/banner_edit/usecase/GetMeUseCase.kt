package com.dertefter.banner_edit.usecase

import com.dertefter.data.dto.me.Me
import com.dertefter.data.repository.MeRepository
import javax.inject.Inject

class GetMeUseCase @Inject constructor(
    private val meRepository: MeRepository,
) {
    suspend operator fun invoke(): Result<Me> {
        return meRepository.updateMe()
    }
}
