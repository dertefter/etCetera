package com.dertefter.event_viewer

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.parkwoocheol.composewebview.ComposeWebView
import com.parkwoocheol.composewebview.WebView
import com.parkwoocheol.composewebview.client.onPageStarted
import com.parkwoocheol.composewebview.client.rememberWebViewClient
import com.parkwoocheol.composewebview.rememberSaveableWebViewState
import com.parkwoocheol.composewebview.rememberWebViewController
import dev.zacsweers.metrox.viewmodel.metroViewModel
import java.io.ByteArrayInputStream

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun EventViewerRoute(
    viewModel: WebViewModel = metroViewModel(),
) {
    val accessToken by viewModel.accessToken.collectAsStateWithLifecycle()
    val currentEvent by viewModel.currentEvent.collectAsStateWithLifecycle(initialValue = null)
    val targetUrl = currentEvent?.url.orEmpty()


    val tokenRef = remember { mutableStateOf(accessToken) }
    tokenRef.value = accessToken

    val client = rememberWebViewClient {
        onPageStarted { _, url, _ ->
            println("Started loading: $url")
        }
    }

    SideEffect {
        if (targetUrl.isNotEmpty()) {
            val cm = CookieManager.getInstance()
            cm.setAcceptCookie(true)
            cm.setCookie(targetUrl, "is_auth=1; Path=/")
            cm.flush()
        }
    }

    val headers = remember { emptyMap<String, String>() }

    if (targetUrl.isNotEmpty()) {
        val state = rememberSaveableWebViewState(
            url = targetUrl,
            additionalHttpHeaders = headers,
        )
        val controller = rememberWebViewController()

        ComposeWebView(
            state = state,
            controller = controller,
            client = client,
            modifier = Modifier.fillMaxSize(),
            onCreated = { webView ->
                CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

                webView.setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)

                webView.settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    useWideViewPort = true
                }

                // ── Перехват на уровне WebView ──
                webView.webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val path = request?.url?.path ?: ""

                        if (request?.method == "POST" &&
                            path.contains("/api/v1/auth/refresh")
                        ) {
                            val token = tokenRef.value.orEmpty()
                            Log.d("WebRoute", "Intercepted refresh → native token")

                            val json = """{"accessToken":"$token"}"""
                            return WebResourceResponse(
                                "application/json",
                                "UTF-8",
                                200,
                                "OK",
                                mapOf(
                                    "Content-Type" to "application/json",
                                    "Access-Control-Allow-Origin" to "*"
                                ),
                                ByteArrayInputStream(json.toByteArray(Charsets.UTF_8))
                            )
                        }

                        return super.shouldInterceptRequest(view, request)
                    }
                }
            },
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        )
    }
}
