package com.dertefter.comments.usecase

import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import javax.inject.Inject

class OpenAsBottomSheetUseCase @Inject constructor(
    private val navigator: Navigator
) {
    operator fun invoke(route: Routes) {
        navigator.openAsBottomSheet(route)
    }
}