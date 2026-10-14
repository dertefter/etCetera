package com.dertefter.etcetera.navigation

import com.dertefter.navigation.NavigationAction
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
class AppNavigator : Navigator {

    private val _navigationActions = Channel<NavigationAction>()
    override val navigationActions: Flow<NavigationAction> = _navigationActions.receiveAsFlow()

    override fun navigate(route: Routes) {
        _navigationActions.trySend(NavigationAction.Navigate(route))
    }

    override fun openAsBottomSheet(route: Routes) {
        //dont need it
    }

    override fun hideBottomSheet() {
        //dont need it
    }

    override fun navigateUp() {
        _navigationActions.trySend(NavigationAction.NavigateUp)
    }

    override fun navigateAndClearBackStack(route: Routes, popupTo: Routes, inclusive: Boolean) {
        _navigationActions.trySend(NavigationAction.NavigateAndClearBackStack(route, popupTo, inclusive))
    }
}
