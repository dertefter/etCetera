package com.dertefter.ai.di

import com.dertefter.ai.repository.AiRepository
import com.dertefter.ai.repository.AiRepositoryImpl
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@BindingContainer
@ContributesTo(AppScope::class)
interface AiModule {
    @Binds
    fun bindAiRepository(impl: AiRepositoryImpl): AiRepository
}
