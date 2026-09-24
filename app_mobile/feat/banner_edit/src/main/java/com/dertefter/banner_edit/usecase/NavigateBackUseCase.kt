package com.dertefter.banner_edit.usecase

import com.dertefter.navigation.Navigator
import javax.inject.Inject

class NavigateBackUseCase @Inject constructor(
    private val navigator: Navigator
) {
    operator fun invoke() {
        navigator.navigateUp()
    }
}