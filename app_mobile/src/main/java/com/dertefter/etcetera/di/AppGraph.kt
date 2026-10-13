package com.dertefter.etcetera.di

import android.app.Application
import com.dertefter.data.repository.AuthRepository
import com.dertefter.data.repository.CrashlyticsRepository
import com.dertefter.data.datasource.local.TokenManager
import dev.zacsweers.metro.AppScope
import com.dertefter.data.repository.SearchRepository
import com.dertefter.navigation.Navigator
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(AppScope::class)
interface AppGraph : ViewModelGraph {
    val crashlyticsRepository: CrashlyticsRepository
    val navigator: Navigator
    val searchRepository: SearchRepository
    val authRepository: AuthRepository
    val tokenManager: TokenManager
    val viewModelFactory: dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AppGraph
    }
}
