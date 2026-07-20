package com.dertefter.etcetera.di

import com.dertefter.data.di.IsWearDevice
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DeviceModule {
    @Provides
    @IsWearDevice
    fun provideIsWearDevice(): Boolean = false
}
