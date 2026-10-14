package com.dertefter.user.usecase

import com.dertefter.data.dto.me.Me
import com.dertefter.data.repository.MeRepository
import kotlinx.coroutines.flow.Flow
import dev.zacsweers.metro.Inject

@Inject
class GetMeUseCase(
    private val meRepository: MeRepository
) {
    operator fun invoke(): Flow<Me?> = meRepository.me
}
