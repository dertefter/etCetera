package com.dertefter.data.common

import android.util.Log
import com.dertefter.data.repository.CrashlyticsRepository
import kotlinx.coroutines.CancellationException

/**
 * Logs the error to CrashlyticsRepository if the result is a failure.
 */
fun <T> Result<T>.onFailureLog(repository: CrashlyticsRepository): Result<T> = onFailure {
    Log.e("onFailureLog", it.stackTraceToString())
    if (it !is CancellationException) {
        repository.showError(it)
    }
}
