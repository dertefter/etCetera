package com.dertefter.event_viewer

import android.util.Log
import android.webkit.JavascriptInterface


internal class EventNativeBridge(
    private val onRequestAuth: (expired: Boolean) -> Unit,
    private val onClose: () -> Unit,
    private val onOpenSocial: (String) -> Unit,
) {

    @JavascriptInterface
    fun requestAuth() {
        onRequestAuth(false)
    }

    @JavascriptInterface
    fun onTokenExpired() {
        onRequestAuth(true)
    }

    @JavascriptInterface
    fun closeEvent() {
        onClose()
    }

    @JavascriptInterface
    fun ownsClose() {
        Log.d(TAG, "Страница закрывает ивент самостоятельно")
    }

    @JavascriptInterface
    fun openSocial(path: String?) {
        if (!path.isNullOrBlank()) {
            onOpenSocial(path)
        }
    }

    private companion object {
        const val TAG = "EventNativeBridge"
    }
}
