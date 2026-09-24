package com.dertefter.user.usecase

import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import javax.inject.Inject

class NavigateToScreenUseCase @Inject constructor(
    private val navigator: Navigator
) {
    operator fun invoke(route: Routes) {
        navigator.navigate(route)
    }
}
