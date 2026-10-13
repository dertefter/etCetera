package com.dertefter.user.usecase

import com.dertefter.navigation.Navigator
import dev.zacsweers.metro.Inject

class NavigateBackUseCase @Inject constructor(
    private val navigator: Navigator
) {
    operator fun invoke() {
        navigator.navigateUp()
    }
}