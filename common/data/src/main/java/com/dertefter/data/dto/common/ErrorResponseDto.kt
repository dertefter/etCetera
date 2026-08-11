package com.dertefter.data.dto.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(
    @SerialName("error") val error: ErrorDto? = null
)

@Serializable
data class ErrorDto(
    @SerialName("code") val code: String? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("canRestore") val canRestore: Boolean? = null,
    @SerialName("retryAfter") val retryAfter: Int? = null
)
