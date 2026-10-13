package com.dertefter.comments.usecase

import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import dev.zacsweers.metro.Inject

class NavigateToScreenUseCase @Inject constructor(
    private val navigator: Navigator
) {
    operator fun invoke(route: Routes) {
        navigator.navigate(route)
    }
}