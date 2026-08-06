package com.dertefter.data.common

import com.dertefter.data.repository.CrashlyticsRepository
import kotlinx.coroutines.CancellationException

/**
 * Logs the error to CrashlyticsRepository if the result is a failure.
 */
inline fun <T> Result<T>.onFailureLog(repository: CrashlyticsRepository): Result<T> = onFailure {
    if (it !is CancellationException) {
        repository.showError(it)
    }
}
