package com.dertefter.data.repository

import android.content.Context
import com.dertefter.data.dto.app.CrashlyticsItemDto
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class CrashlyticsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : CrashlyticsRepository {

    private val crashLogsFolder: File by lazy {
        File(context.filesDir, "crash_logs").apply {
            if (!exists()) mkdirs()
        }
    }

    override fun saveCrashLog(exception: Throwable) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        val errorName = exception.javaClass.simpleName
        val fileName = "${timestamp}_$errorName.txt"
        val file = File(crashLogsFolder, fileName)

        val content = buildString {
            appendLine("Timestamp: $timestamp")
            appendLine("Exception: ${exception.javaClass.name}")
            appendLine("Message: ${exception.message}")
            appendLine("Stacktrace:")
            appendLine(exception.stackTraceToString())
        }

        try {
            file.writeText(content)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getCrashLogsList(): List<CrashlyticsItemDto> {
        return try {
            crashLogsFolder.listFiles()
                ?.filter { it.isFile }
                ?.map { CrashlyticsItemDto(it.name, it.absolutePath) }
                ?.sortedByDescending { it.name } ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    override fun deleteCrashLog(path: String) {
        try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun readCrashLog(path: String): String {
        return try {
            File(path).readText()
        } catch (e: Exception) {
            "Error reading log: ${e.message}"
        }
    }
}
