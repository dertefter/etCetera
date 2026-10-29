package com.dertefter.ai.repository

import android.content.Context
import android.net.Uri
import com.dertefter.ai.model.AiBackend
import com.dertefter.ai.model.AiError
import com.dertefter.ai.model.AiModel
import com.dertefter.ai.model.AiModelConfig
import com.dertefter.ai.model.AiState
import com.dertefter.ai.model.SummarizeOptions
import com.dertefter.ai.prompt.SummarizePrompt
import com.dertefter.ai.storage.AiModelStorage
import com.dertefter.ai.store.AiSettingsStore
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.Message
import com.google.ai.edge.litertlm.SamplerConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.coroutineContext

@SingleIn(AppScope::class)
@Inject
class AiRepositoryImpl(
    private val context: Context,
    private val settingsStore: AiSettingsStore,
    private val modelStorage: AiModelStorage,
) : AiRepository {

    private val mutex = Mutex()

    private val mutableState = MutableStateFlow<AiState>(AiState.Idle)
    override val state: StateFlow<AiState> = mutableState.asStateFlow()

    override val config: Flow<AiModelConfig?> = settingsStore.config

    private val modelsRevision = MutableStateFlow(0)

    override val models: Flow<List<AiModel>> = modelsRevision.map {
        withContext(Dispatchers.IO) { modelStorage.list() }
    }

    private var engine: Engine? = null

    override suspend fun configure(config: AiModelConfig): Result<Unit> = handle<Unit> {
        mutex.withLock {
            if (settingsStore.config.first() == config) return@withLock
            settingsStore.save(config)
            closeEngine()
        }
    }

    override suspend fun importModel(
        sourcePath: String,
        name: String?,
    ): Result<AiModel> = handle {
        mutex.withLock {
            val model = modelStorage.import(sourcePath, name)
            modelsRevision.value += 1
            model
        }
    }

    override suspend fun importModel(
        uri: Uri,
        name: String?,
    ): Result<AiModel> = handle {
        mutex.withLock {
            val model = modelStorage.import(uri, name)
            modelsRevision.value += 1
            model
        }
    }

    override suspend fun deleteModel(modelId: String): Result<Unit> = handle<Unit> {
        mutex.withLock {
            val model = modelStorage.list().firstOrNull { it.id == modelId }
                ?: throw AiError.ModelNotFound(modelId)
            modelStorage.delete(model)
            modelsRevision.value += 1
            // Удалили модель, на которой работает движок — конфигурация больше не валидна.
            if (settingsStore.config.first()?.modelPath == model.path) {
                settingsStore.clear()
                closeEngine()
            }
        }
    }

    override suspend fun prepare(): Result<Unit> = handle<Unit> {
        mutex.withLock {
            acquireEngine()
        }
    }

    override suspend fun summarize(
        text: String,
        options: SummarizeOptions,
    ): Result<String> = handle {
        val source = text.trim()
        if (source.isEmpty()) throw AiError.EmptyInput
        mutex.withLock {
            val activeEngine = acquireEngine()
            generate(activeEngine, source, options)
        }
    }

    override suspend fun release() {
        mutex.withLock {
            closeEngine()
        }
    }

    private suspend fun acquireEngine(): Engine {
        engine?.let { loaded ->
            if (loaded.isInitialized()) return loaded
            runCatching { loaded.close() }
            engine = null
        }
        val config = settingsStore.config.first() ?: throw AiError.NotConfigured
        if (!File(config.modelPath).isFile) throw AiError.ModelNotFound(config.modelPath)
        mutableState.value = AiState.Preparing
        return try {
            val loaded = withContext(Dispatchers.Default) { createEngine(config) }
            engine = loaded
            mutableState.value = AiState.Ready
            loaded
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            val aiError = AiError.EngineInitializationFailed(error)
            mutableState.value = AiState.Failed(aiError)
            throw aiError
        }
    }

    private fun createEngine(config: AiModelConfig): Engine {
        val engineConfig = EngineConfig(
            modelPath = config.modelPath,
            backend = config.backend.toLitertBackend(),
            maxNumTokens = config.maxNumTokens,
            cacheDir = config.cacheDir ?: context.cacheDir.path,
        )
        val created = Engine(engineConfig)
        return try {
            created.initialize()
            created
        } catch (error: Throwable) {
            runCatching { created.close() }
            throw error
        }
    }

    private fun closeEngine() {
        engine?.let { loaded -> runCatching { loaded.close() } }
        engine = null
        mutableState.value = AiState.Idle
    }

    private suspend fun generate(
        activeEngine: Engine,
        source: String,
        options: SummarizeOptions,
    ): String {
        val conversation = try {
            activeEngine.createConversation(
                ConversationConfig(
                    systemInstruction = Contents.of(SummarizePrompt.system(options)),
                    samplerConfig = SAMPLER_CONFIG,
                    maxOutputToken = options.maxWords?.let { it * TOKENS_PER_WORD + OUTPUT_TOKENS_MARGIN },
                )
            )
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            throw AiError.GenerationFailed(error)
        }
        // Нативная генерация не реагирует на отмену корутины, поэтому останавливаем её явно.
        val cancellationWatcher = currentCoroutineContext()[Job]?.invokeOnCompletion { cause ->
            if (cause is CancellationException) runCatching { conversation.cancelProcess() }
        }
        return try {
            val message = withContext(Dispatchers.Default) {
                conversation.sendMessage(SummarizePrompt.user(source))
            }
            val result = message.text().cleanUp()
            if (result.isEmpty()) throw AiError.EmptyResponse
            result
        } catch (error: Throwable) {
            if (error is CancellationException || error is AiError) throw error
            throw AiError.GenerationFailed(error)
        } finally {
            cancellationWatcher?.dispose()
            runCatching { conversation.close() }
        }
    }

    private fun String.cleanUp(): String {
        val trimmed = trim()
            .removeSurrounding("\"")
            .removeSurrounding("“", "”")
            .removeSurrounding("«", "»")
            .trim()
        return trimmed.replace(SERVICE_PREFIX_REGEX, "").trim()
    }

    private fun Message.text(): String =
        contents.contents
            .filterIsInstance<Content.Text>()
            .joinToString(separator = "") { it.text }

    private suspend fun <T> handle(block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (error: CancellationException) {
            throw error
        } catch (error: AiError) {
            Result.failure(error)
        } catch (error: Throwable) {
            Result.failure(AiError.Unexpected(error))
        }

    private fun AiBackend.toLitertBackend(): Backend = when (this) {
        AiBackend.Gpu -> Backend.GPU()
        is AiBackend.Cpu -> Backend.CPU(threadCount = threadCount)
        is AiBackend.Npu -> Backend.NPU(nativeLibraryDir = nativeLibraryDir)
        AiBackend.GoogleTensor -> Backend.GOOGLE_TENSOR()
    }

    private companion object {
        val SAMPLER_CONFIG = SamplerConfig(topK = 40, topP = 0.95, temperature = 0.2)

        const val TOKENS_PER_WORD = 3

        const val OUTPUT_TOKENS_MARGIN = 64

        val SERVICE_PREFIX_REGEX = Regex(
            "^(summary|shortened text|short version|краткое содержание|краткий пересказ|кратко|сокращённый текст)\\s*[:\\-—]\\s*",
            RegexOption.IGNORE_CASE,
        )
    }
}
