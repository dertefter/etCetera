package com.dertefter.etcetera.di

import com.dertefter.data.repository.SearchRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun searchRepository(): SearchRepository
}
