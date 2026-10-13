package com.dertefter.etcetera.presentation

import android.app.Application
import com.dertefter.etcetera.di.AppGraph
import dev.zacsweers.metro.createGraphFactory

class EtCetera : Application() {

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
        } catch (_: Exception) {
        }
    }

}
