package com.dertefter.banner_edit.usecase

import com.dertefter.navigation.Navigator
import dev.zacsweers.metro.Inject

class NavigateBackUseCase @Inject constructor(
    private val navigator: Navigator
) {
    operator fun invoke() {
        navigator.navigateUp()
    }
}