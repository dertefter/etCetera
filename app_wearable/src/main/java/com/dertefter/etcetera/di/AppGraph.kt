package com.dertefter.etcetera.di

import android.app.Application
import dev.zacsweers.metro.AppScope
import com.dertefter.data.repository.CrashlyticsRepository
import com.dertefter.navigation.Navigator
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(AppScope::class)
interface AppGraph : ViewModelGraph {
    val crashlyticsRepository: CrashlyticsRepository
    val navigator: Navigator
    val viewModelFactory: MetroViewModelFactory

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AppGraph
    }
}
