package com.dertefter.banner_edit.usecase

import com.dertefter.navigation.Navigator
import dev.zacsweers.metro.Inject

@Inject
class NavigateBackUseCase(
    private val navigator: Navigator
) {
    operator fun invoke() {
        navigator.navigateUp()
    }
}