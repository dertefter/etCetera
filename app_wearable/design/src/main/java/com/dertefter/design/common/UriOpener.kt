package com.dertefter.design.common

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.wear.remote.interactions.RemoteActivityHelper
import androidx.core.net.toUri

@Composable
fun rememberOpenUriOnPhone(): (url: String) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        val remoteActivityHelper = RemoteActivityHelper(context.applicationContext)
        val openUri: (String) -> Unit = { url ->
            val intent = Intent(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(url.toUri())
            runCatching { remoteActivityHelper.startRemoteActivity(intent) }
        }
        openUri
    }
}