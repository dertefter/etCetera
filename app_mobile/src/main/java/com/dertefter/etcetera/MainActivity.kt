package com.dertefter.etcetera

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.design.theme.AppTheme
import com.dertefter.etcetera.navigation.AppNavHost
import com.dertefter.etcetera.navigation.DeepLinkParser
import com.dertefter.etcetera.presentation.MainScreen
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import com.dertefter.design.theme.EmojiAvatarHarmonizationColor as DesignColor

class MainActivity : ComponentActivity() {

    private val navigator by lazy { (applicationContext as EtCetera).appGraph.navigator }
    private val viewModelFactory by lazy { (applicationContext as EtCetera).appGraph.viewModelFactory }

    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            handleDeepLink(intent)
        }

        splashScreen.setKeepOnScreenCondition {
            !viewModel.isReady.value
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        setContent {
            CompositionLocalProvider(LocalMetroViewModelFactory provides viewModelFactory) {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                val themeState by viewModel.themeState.collectAsStateWithLifecycle()

                if (uiState.isReady){
                    themeState?.let { themeState ->
                        val harmonizationColor =
                            DesignColor.valueOf(themeState.emojiHarmonizationColor.name)
                        val darkTheme = themeState.darkTheme

                        AppTheme(
                            emojiAvatarHarmonizeColor = harmonizationColor,
                            darkTheme = darkTheme,
                            postHorizontalExtraSpace = themeState.postHorizontalExtraSpace,
                            postContained = themeState.postContained,
                            postShowUsername = themeState.postShowUsername,
                            postSwapDateAndUsername = themeState.postSwapDateAndUsername,
                            navFloating = themeState.navFloating,
                            navLabeled = themeState.navLabeled,
                            navBlurred = themeState.navBlurred,
                            appBarBlurred = themeState.appBarBlurred,
                            appBarFaded = themeState.appBarFaded
                        ) {
                            MainScreen(
                                navigator,
                                uiState = uiState,
                                themeState = themeState,
                                appNavHost = { entries, onBack, modifier ->
                                    AppNavHost(
                                        entries = entries,
                                        onBack = onBack,
                                        modifier = modifier
                                    )
                                }
                            )
                        }

                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val route = DeepLinkParser.parse(intent?.data)
        route?.let { navigator.navigate(it) }
    }
}
