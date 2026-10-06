package com.dertefter.data.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    val email: String,
    val password: String,
    val turnstileToken: String
)
