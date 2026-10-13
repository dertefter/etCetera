package com.dertefter.data.di

import dev.zacsweers.metro.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SettingsDataStore
