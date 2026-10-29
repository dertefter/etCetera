package com.dertefter.event_viewer

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.design.theme.bottomNavHeight
import com.parkwoocheol.composewebview.ComposeWebView
import com.parkwoocheol.composewebview.DarkMode
import com.parkwoocheol.composewebview.LoadingState
import com.parkwoocheol.composewebview.WebViewSettings
import com.parkwoocheol.composewebview.client.rememberWebViewClient
import com.parkwoocheol.composewebview.client.shouldInterceptRequest
import com.parkwoocheol.composewebview.createPlatformWebResourceResponse
import com.parkwoocheol.composewebview.rememberSaveableWebViewState
import com.parkwoocheol.composewebview.rememberWebViewController
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import kotlin.time.Duration.Companion.milliseconds

private const val TOKEN_WAIT_TIMEOUT_MS = 4_000L

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun EventViewerRoute(
    viewModel: WebViewModel = metroViewModel(),
) {
    val accessToken: State<String?> = viewModel.accessToken.collectAsStateWithLifecycle()
    val currentEvent by viewModel.currentEvent.collectAsStateWithLifecycle(initialValue = null)
    val targetUrl = currentEvent?.url.orEmpty()

    val controller = rememberWebViewController()
    val scope = rememberCoroutineScope()

    val pushToken: (String?) -> Unit = remember(controller) {
        { token ->
            val payload = JSONObject().put("token", token.orEmpty()).put("canClose", true)
            controller.evaluateJavascript("window.setNativeAuth($payload);", null)
        }
    }

    val bridge = remember(controller, viewModel) {
        EventNativeBridge(
            onRequestAuth = { expired ->
                scope.launch {
                    if (expired) {
                        viewModel.refreshAccessToken()
                    }
                    val token =
                        accessToken.value ?: withTimeoutOrNull(TOKEN_WAIT_TIMEOUT_MS.milliseconds) {
                            viewModel.accessToken.first { it != null }
                        }
                    pushToken(token)
                }
            },
            onClose = viewModel::closeEvent,
            onOpenSocial = viewModel::openSocial,
        )
    }

    val density = LocalDensity.current
    val topInset = (WindowInsets.systemBars.getTop(density) / density.density).toInt()
    val bottomInset = (MaterialTheme.bottomNavHeight).value.toInt()

    val insetsScript = "window.setNativeInsets({top:$topInset,right:0,bottom:$bottomInset,left:0});"

    val client = rememberWebViewClient {
        shouldInterceptRequest { _, request ->
            if (request?.method == "POST" && request.url.contains("/api/v1/auth/refresh")) {
                Log.d("WebRoute", "Intercepted refresh → native token")

                createPlatformWebResourceResponse(
                    mimeType = "application/json",
                    encoding = "UTF-8",
                    data = """{"accessToken":"${accessToken.value.orEmpty()}"}""".toByteArray(
                        Charsets.UTF_8
                    ),
                    statusCode = 200,
                    reasonPhrase = "OK",
                    responseHeaders = mapOf(
                        "Content-Type" to "application/json",
                        "Access-Control-Allow-Origin" to "*",
                    ),
                )
            } else {
                null
            }
        }
    }

    if (targetUrl.isNotEmpty()) {
        val state = rememberSaveableWebViewState(url = targetUrl)
        LaunchedEffect(state.loadingState, insetsScript) {
            if (state.loadingState is LoadingState.Finished) {
                controller.evaluateJavascript(insetsScript, null)
            }
        }

        ComposeWebView(
            state = state,
            controller = controller,
            settings = WebViewSettings(darkMode = DarkMode.LIGHT),
            client = client,
            modifier = Modifier.fillMaxSize(),
            javaScriptInterfaces = remember(bridge) { mapOf("AndroidBridge" to bridge) },
            onCreated = { webView ->
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

                webView.settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    useWideViewPort = true
                }
            },
        )

    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        )
    }
}
