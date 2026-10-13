package com.dertefter.etcetera.di

import com.dertefter.data.repository.SearchRepository

interface WidgetEntryPoint {
    fun searchRepository(): SearchRepository
}
