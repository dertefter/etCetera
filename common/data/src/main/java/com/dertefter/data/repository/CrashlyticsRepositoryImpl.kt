package com.dertefter.data.repository

import android.content.Context
import android.util.Log
import com.dertefter.data.common.AppError
import com.dertefter.data.common.toAppError
import com.dertefter.data.dto.app.CrashlyticsItem
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@SingleIn(AppScope::class)
@Inject
class CrashlyticsRepositoryImpl(
    private val context: Context,
) : CrashlyticsRepository {

    private val crashLogsFolder: File by lazy {
        File(context.filesDir, "crash_logs").apply {
            if (!exists()) mkdirs()
        }
    }

    private data class TimestampedError(val error: AppError?, val timestamp: Long = System.currentTimeMillis())

    private val _currentError = MutableStateFlow<TimestampedError>(TimestampedError(null))
    override val currentError: Flow<AppError?> = _currentError.map { it.error }

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var resetJob: Job? = null

    override fun showError(e: Throwable?) {
        Log.e("showError", "Throwable: $e, stack: ${e?.stackTraceToString()}")
        val appError = e?.toAppError()
        Log.e("showError", "Mapped appError: $appError")
        resetJob?.cancel()
        if (appError != null) {
            _currentError.value = TimestampedError(appError, System.currentTimeMillis())
            Log.e("showError", "Set _currentError.value to: $appError")
            resetJob = scope.launch {
                delay(2000L.milliseconds)
                Log.e("showError", "Resetting _currentError.value to null")
                _currentError.value = TimestampedError(null, System.currentTimeMillis())
            }
        } else {
            _currentError.value = TimestampedError(null, System.currentTimeMillis())
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

    override fun getCrashLogsList(): List<CrashlyticsItem> {
        return try {
            crashLogsFolder.listFiles()
                ?.filter { it.isFile }
                ?.map { CrashlyticsItem(it.name, it.absolutePath) }
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
