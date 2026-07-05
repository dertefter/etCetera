package com.dertefter.data.repository

import com.dertefter.data.dto.app.CrashlyticsItemDto

interface CrashlyticsRepository {

    fun saveCrashLog(exception: Throwable) // save file with name current date and time and err name and stacktrace in file

    fun getCrashLogsList(): List<CrashlyticsItemDto> // list Of file names

    fun deleteCrashLog(path: String)

    fun readCrashLog(path: String): String
}
