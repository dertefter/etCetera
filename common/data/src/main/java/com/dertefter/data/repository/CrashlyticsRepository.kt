package com.dertefter.data.repository

import com.dertefter.data.common.AppError
import com.dertefter.data.dto.app.CrashlyticsItem
import kotlinx.coroutines.flow.Flow

interface CrashlyticsRepository {

    fun saveCrashLog(exception: Throwable) // save file with name current date and time and err name and stacktrace in file

    fun getCrashLogsList(): List<CrashlyticsItem> // list Of file names

    fun deleteCrashLog(path: String)

    fun readCrashLog(path: String): String

    val currentError: Flow<AppError?>

    fun showError(e: Throwable?)

}
