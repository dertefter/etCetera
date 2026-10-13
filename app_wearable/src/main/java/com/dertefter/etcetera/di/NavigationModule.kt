package com.dertefter.etcetera.di

import dev.zacsweers.metro.AppScope
import com.dertefter.etcetera.navigation.AppNavigator
import com.dertefter.navigation.Navigator
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(AppScope::class)
interface NavigationModule {

    @Binds
    fun bindNavigator(
        appNavigator: AppNavigator
    ): Navigator
}
