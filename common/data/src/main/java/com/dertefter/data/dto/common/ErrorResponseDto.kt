package com.dertefter.data.dto.common

import com.google.gson.annotations.SerializedName

data class ErrorResponseDto(
    @SerializedName("error") val error: ErrorDto? = null
)

data class ErrorDto(
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("canRestore") val canRestore: Boolean? = null,
    @SerializedName("retryAfter") val retryAfter: Int? = null
)