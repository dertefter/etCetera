package com.dertefter.data.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("turnstileToken") val turnstileToken: String
)
