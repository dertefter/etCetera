package com.dertefter.comments.usecase

import com.dertefter.data.dto.me.Me
import com.dertefter.data.repository.MeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMeUseCase @Inject constructor(
    private val meRepository: MeRepository
) {
    operator fun invoke(): Flow<Me?> = meRepository.me
}
