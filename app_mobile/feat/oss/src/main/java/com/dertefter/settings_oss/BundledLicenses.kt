package com.dertefter.settings_oss

import android.content.Context
import androidx.annotation.RawRes
import com.github.droibit.oss_licenses.parser.OssLicense
import com.dertefter.oss.R

internal fun bundledLicenses(context: Context): List<OssLicense> = listOf(
    OssLicense(
        library = context.getString(R.string.settings_oss_google_noto_emoji),
        text = readRawText(context, com.dertefter.design.R.raw.google_noto_emoji_ofl),
    ),
)

private fun readRawText(context: Context, @RawRes resId: Int): String =
    context.resources.openRawResource(resId).use { stream ->
        stream.bufferedReader().readText()
    }
