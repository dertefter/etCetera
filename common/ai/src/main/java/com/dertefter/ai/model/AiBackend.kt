package com.dertefter.ai.model


sealed interface AiBackend {

    data object Gpu : AiBackend

    data class Cpu(val threadCount: Int? = null) : AiBackend

    data class Npu(val nativeLibraryDir: String) : AiBackend

    data object GoogleTensor : AiBackend
}
