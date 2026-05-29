package com.dertefter.data.dto.auth

import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("accessToken") val accessToken: String
)