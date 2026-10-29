package com.dertefter.ai.model

data class GenerateOptions(
    val maxWords: Int? = null,
    val language: String? = null,
) {
    init {
        require(maxWords == null || maxWords > 0) { "maxWords должен быть положительным" }
    }
}
