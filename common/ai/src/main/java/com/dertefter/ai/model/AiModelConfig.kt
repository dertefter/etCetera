package com.dertefter.ai.model

data class AiModelConfig(
    val modelPath: String,
    val backend: AiBackend = AiBackend.Gpu,
    val maxNumTokens: Int? = null,
    val cacheDir: String? = null,
)
