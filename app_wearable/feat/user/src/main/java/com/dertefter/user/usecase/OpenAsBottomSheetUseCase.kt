package com.dertefter.user.usecase

import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import dev.zacsweers.metro.Inject

@Inject
class OpenAsBottomSheetUseCase(
    private val navigator: Navigator
) {
    operator fun invoke(route: Routes) {
        navigator.openAsBottomSheet(route)
    }
}
