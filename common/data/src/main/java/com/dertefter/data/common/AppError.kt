package com.dertefter.data.common

import java.io.IOException

sealed interface AppError {
    val message: String?

    data class Network(override val message: String?) : AppError
    data class Unexpected(override val message: String?) : AppError
}

fun Throwable.toAppError(): AppError {
    val errorMessage = this.localizedMessage ?: this.message

    return when (this) {
        is IOException -> AppError.Network(errorMessage)
        else -> AppError.Unexpected(errorMessage)
    }
}