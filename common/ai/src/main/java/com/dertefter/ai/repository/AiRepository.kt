package com.dertefter.ai.repository

import android.net.Uri
import com.dertefter.ai.model.AiModel
import com.dertefter.ai.model.AiModelConfig
import com.dertefter.ai.model.AiState
import com.dertefter.ai.model.SummarizeOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


interface AiRepository {

    val state: StateFlow<AiState>

    val config: Flow<AiModelConfig?>

    val models: Flow<List<AiModel>>

    suspend fun importModel(
        sourcePath: String,
        name: String? = null,
    ): Result<AiModel>

    suspend fun importModel(
        uri: Uri,
        name: String? = null,
    ): Result<AiModel>

    suspend fun deleteModel(modelId: String): Result<Unit>

    suspend fun configure(config: AiModelConfig): Result<Unit>

    suspend fun prepare(): Result<Unit>

    suspend fun summarize(
        text: String,
        options: SummarizeOptions = SummarizeOptions(),
    ): Result<String>

    suspend fun release()
}
