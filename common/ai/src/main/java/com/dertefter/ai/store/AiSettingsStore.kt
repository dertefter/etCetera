package com.dertefter.ai.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dertefter.ai.di.AiDataStore
import com.dertefter.ai.model.AiBackend
import com.dertefter.ai.model.AiModelConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

@SingleIn(AppScope::class)
@Inject
class AiSettingsStore(
    @AiDataStore private val dataStore: DataStore<Preferences>,
) {

    val config: Flow<AiModelConfig?> = dataStore.data
        .catch { error ->
            // Битый файл настроек не должен ломать сценарий: считаем, что конфигурации нет.
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences -> preferences.toModelConfig() }

    suspend fun save(config: AiModelConfig) {
        dataStore.edit { preferences -> preferences.write(config) }
    }

    suspend fun clear() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    private fun Preferences.toModelConfig(): AiModelConfig? {
        val modelPath = this[MODEL_PATH] ?: return null
        return AiModelConfig(
            modelPath = modelPath,
            backend = backend(),
            maxNumTokens = this[MAX_NUM_TOKENS],
            cacheDir = this[CACHE_DIR],
        )
    }

    private fun Preferences.backend(): AiBackend = when (this[BACKEND_TYPE]) {
        BACKEND_CPU -> AiBackend.Cpu(this[CPU_THREAD_COUNT])
        BACKEND_NPU -> AiBackend.Npu(this[NPU_NATIVE_LIBRARY_DIR].orEmpty())
        BACKEND_GOOGLE_TENSOR -> AiBackend.GoogleTensor
        // Неизвестное или отсутствующее значение — самый совместимый вариант.
        else -> AiBackend.Gpu
    }

    private fun MutablePreferences.write(config: AiModelConfig) {
        this[MODEL_PATH] = config.modelPath
        writeBackend(config.backend)
        writeOrRemove(MAX_NUM_TOKENS, config.maxNumTokens)
        writeOrRemove(CACHE_DIR, config.cacheDir)
    }

    private fun MutablePreferences.writeBackend(backend: AiBackend) {
        writeOrRemove(CPU_THREAD_COUNT, null)
        writeOrRemove(NPU_NATIVE_LIBRARY_DIR, null)
        when (backend) {
            AiBackend.Gpu -> this[BACKEND_TYPE] = BACKEND_GPU
            is AiBackend.Cpu -> {
                this[BACKEND_TYPE] = BACKEND_CPU
                backend.threadCount?.let { this[CPU_THREAD_COUNT] = it }
            }
            is AiBackend.Npu -> {
                this[BACKEND_TYPE] = BACKEND_NPU
                this[NPU_NATIVE_LIBRARY_DIR] = backend.nativeLibraryDir
            }
            AiBackend.GoogleTensor -> this[BACKEND_TYPE] = BACKEND_GOOGLE_TENSOR
        }
    }

    private fun MutablePreferences.writeOrRemove(key: Preferences.Key<Int>, value: Int?) {
        if (value == null) remove(key) else this[key] = value
    }

    private fun MutablePreferences.writeOrRemove(key: Preferences.Key<String>, value: String?) {
        if (value == null) remove(key) else this[key] = value
    }

    private companion object {
        val MODEL_PATH = stringPreferencesKey("model_path")
        val BACKEND_TYPE = stringPreferencesKey("backend_type")
        val CPU_THREAD_COUNT = intPreferencesKey("cpu_thread_count")
        val NPU_NATIVE_LIBRARY_DIR = stringPreferencesKey("npu_native_library_dir")
        val MAX_NUM_TOKENS = intPreferencesKey("max_num_tokens")
        val CACHE_DIR = stringPreferencesKey("cache_dir")

        const val BACKEND_GPU = "gpu"
        const val BACKEND_CPU = "cpu"
        const val BACKEND_NPU = "npu"
        const val BACKEND_GOOGLE_TENSOR = "google_tensor"
    }
}
