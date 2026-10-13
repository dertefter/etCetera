package com.dertefter.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
object DataStoreModule {

    @Provides
    fun provideContext(application: android.app.Application): Context = application

    @Provides
    @SingleIn(AppScope::class)
    @AuthDataStore
    fun provideAuthDataStore(context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("auth_prefs") }
        )
    }

    @Provides
    @SingleIn(AppScope::class)
    @SettingsDataStore
    fun provideSettingsDataStore(context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("settings_prefs") }
        )
    }
}
