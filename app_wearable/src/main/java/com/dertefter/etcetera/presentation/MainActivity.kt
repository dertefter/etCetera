package com.dertefter.etcetera.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.wear.compose.material3.AppScaffold
import com.dertefter.design.theme.WearableTheme
import com.dertefter.etcetera.navigation.AppNavHost
import com.dertefter.navigation.NavigationAction
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        viewModel // Force initialization to start listening for data changes

        splashScreen.setKeepOnScreenCondition {
            viewModel.mainScreenState.value.isAuthorized == null
        }

        setContent {
            val mainScreenState by viewModel.mainScreenState.collectAsStateWithLifecycle()
            val isAuthorized = mainScreenState.isAuthorized ?: return@setContent
            WearableTheme {
                val startDestination = if (isAuthorized) Routes.Feed else Routes.Auth
                val backStack = rememberNavBackStack(startDestination)

                LaunchedEffect(isAuthorized) {
                    if (isAuthorized) {
                        if (backStack.contains(Routes.Auth)) {
                            backStack.clear()
                            backStack.add(Routes.Feed)
                        }
                    } else {
                        if (!backStack.contains(Routes.Auth)) {
                            backStack.clear()
                            backStack.add(Routes.Auth)
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    navigator.navigationActions.collect { action ->
                        when (action) {
                            is NavigationAction.Navigate -> {
                                backStack.add(action.route)
                            }

                            is NavigationAction.NavigateAndClearBackStack -> {
                                val index = backStack.indexOfLast { it == action.popupTo }
                                if (index != -1) {
                                    val removeIndex = if (action.inclusive) index else index + 1
                                    while (backStack.size > removeIndex) {
                                        backStack.removeAt(backStack.lastIndex)
                                    }
                                }
                                backStack.add(action.route)
                            }

                            NavigationAction.NavigateUp -> {
                                if (backStack.size > 1) {
                                    backStack.removeAt(backStack.lastIndex)
                                }
                            }

                            is NavigationAction.OpenAsBottomSheet -> {
                                backStack.add(action.route)
                            }

                            NavigationAction.HideBottomSheet -> {
                                if (backStack.size > 1) {
                                    backStack.removeAt(backStack.lastIndex)
                                }
                            }
                        }
                    }
                }

                AppScaffold {
                    AppNavHost(backStack = backStack)
                }
            }
        }
    }
}
