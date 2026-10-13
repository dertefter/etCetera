package com.dertefter.etcetera.di

import dev.zacsweers.metro.AppScope
import com.dertefter.data.di.IsWearDevice
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@BindingContainer
@Suppress("SameReturnValue")
@ContributesTo(AppScope::class)
object DeviceModule {
    @Provides
    @IsWearDevice
    fun provideIsWearDevice(): Boolean = true
}
