package com.dertefter.auth.presentation

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.parkwoocheol.composewebview.ComposeWebView
import com.parkwoocheol.composewebview.WebView
import com.parkwoocheol.composewebview.rememberSaveableWebViewStateWithData

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TurnstileWebView(
    modifier: Modifier = Modifier,
    onTokenReceived: (String) -> Unit
) {
    val siteKey = "0x4AAAAAACHhxczw6fJGwPBg"
    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <script src="https://challenges.cloudflare.com/turnstile/v0/api.js" async defer></script>
            <style>
                html, body {
                    height: 100%;
                    margin: 0;
                    padding: 0;
                }
                body {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    padding: 68px;
                    box-sizing: border-box;
                }
            </style>
        </head>
        <body>
            <div class="cf-turnstile" data-sitekey="$siteKey" data-callback="javascriptCallback"></div>
            <script>
                function javascriptCallback(token) {
                    TurnstileInterface.onToken(token);
                }
            </script>
        </body>
        </html>
    """.trimIndent()

    val state = rememberSaveableWebViewStateWithData(
        data = html,
        baseUrl = "https://xn--d1ah4a.com",
        mimeType = "text/html",
        encoding = "UTF-8"
    )

    ComposeWebView(
        state = state,
        modifier = modifier,
        factory = { context ->
            val contextWrapper = object : ContextWrapper(context) {
                override fun getPackageName(): String = "com.itd.app"
            }
            WebView(contextWrapper)
        },
        javaScriptInterfaces = mapOf(
            "TurnstileInterface" to object {
                @JavascriptInterface
                fun onToken(token: String) {
                    Handler(Looper.getMainLooper()).post {
                        onTokenReceived(token)
                    }
                }
            }
        ),
        onCreated = { webView ->
            webView.settings.javaScriptEnabled = true
        }
    )
}
