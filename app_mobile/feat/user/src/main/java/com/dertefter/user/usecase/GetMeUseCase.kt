package com.dertefter.user.usecase

import com.dertefter.data.dto.me.MeDto
import com.dertefter.data.repository.MeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMeUseCase @Inject constructor(
    private val meRepository: MeRepository
) {
    operator fun invoke(): Flow<MeDto?> = meRepository.me
}
