package com.dertefter.etcetera

import android.app.Application
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.request.crossfade
import com.dertefter.etcetera.di.AppGraph
import dev.zacsweers.metro.createGraphFactory

class EtCetera : Application(), SingletonImageLoader.Factory {

    val appGraph: AppGraph by lazy {
        createGraphFactory<AppGraph.Factory>().create(this)
    }

    override fun onCreate() {
        super.onCreate()

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            saveCrashLogToFile(exception)
            defaultHandler?.uncaughtException(thread, exception)
        }

    }

    private fun saveCrashLogToFile(exception: Throwable) {
        try {
            appGraph.crashlyticsRepository.saveCrashLog(exception)
        } catch (_: Exception) { }
    }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader
            .Builder(context)
            .components {
                if (SDK_INT >= 29) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .crossfade(true)
            .build()
    }

}
