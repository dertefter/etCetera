package com.dertefter.user.usecase

import com.dertefter.data.dto.me.Me
import com.dertefter.data.repository.MeRepository
import dev.zacsweers.metro.Inject

@Inject
class UpdateMeUseCase(
    private val meRepository: MeRepository
) {
    suspend operator fun invoke(): Result<Me> {
        return meRepository.updateMe()
    }
}
