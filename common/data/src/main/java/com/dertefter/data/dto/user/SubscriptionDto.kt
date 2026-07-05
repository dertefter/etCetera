package com.dertefter.data.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionDto(
    val isActive: Boolean,
    val expiresAt: String?,
    val autoRenewal: Boolean
)