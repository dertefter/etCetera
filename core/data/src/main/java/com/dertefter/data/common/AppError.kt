package com.dertefter.data.common

import retrofit2.HttpException
import java.io.IOException

sealed interface AppError {
    data object Network : AppError
    data object Unexpected : AppError
}

fun Throwable.toAppError(): AppError {

    return when (this) {
        is IOException -> AppError.Network
        else -> AppError.Unexpected
    }
}
