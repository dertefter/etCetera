package com.dertefter.data.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignInResponse(
    val accessToken: String
)
