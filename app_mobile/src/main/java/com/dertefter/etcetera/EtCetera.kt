package com.dertefter.etcetera

import android.app.Application
import com.dertefter.data.repository.CrashlyticsRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EtCetera : Application() {

    @Inject
    lateinit var crashlyticsRepository: CrashlyticsRepository

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
            crashlyticsRepository.saveCrashLog(exception)
        } catch (e: Exception) {
        }
    }

}
