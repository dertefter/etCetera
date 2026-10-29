package com.dertefter.ai.model

data class AiModel(
    val id: String,
    val name: String,
    val path: String,
    val sizeBytes: Long,
    val importedAt: Long,
)
