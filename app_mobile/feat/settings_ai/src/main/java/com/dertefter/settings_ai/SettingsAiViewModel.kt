package com.dertefter.settings_ai

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.ai.model.AiBackend
import com.dertefter.ai.model.AiError
import com.dertefter.ai.model.AiModel
import com.dertefter.ai.model.AiModelConfig
import com.dertefter.ai.repository.AiRepository
import com.dertefter.navigation.Navigator
import com.dertefter.settings_ai.presentation.Event
import com.dertefter.settings_ai.presentation.UiState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class SettingsAiViewModel(
    private val application: Application,
    private val navigator: Navigator,
    private val aiRepository: AiRepository,
) : ViewModel() {

    private val isImporting = MutableStateFlow(false)

    private val pendingDelete = MutableStateFlow<AiModel?>(null)

    private val error = MutableStateFlow<AiError?>(null)

    private val backends: List<AiBackend> = buildList {
        add(AiBackend.Gpu)
        add(AiBackend.Cpu())
        add(AiBackend.Npu(nativeLibraryDir = application.applicationInfo.nativeLibraryDir))
        if (supportsGoogleTensor()) add(AiBackend.GoogleTensor)
    }

    private val library = combine(
        aiRepository.models,
        aiRepository.config,
    ) { models, config ->
        Library(
            models = models,
            activeModelId = models.firstOrNull { model -> model.path == config?.modelPath }?.id,
            activeBackend = config?.backend ?: AiBackend.Gpu,
        )
    }

    val uiState: StateFlow<UiState> = combine(
        library,
        isImporting,
        pendingDelete,
        error,
    ) { library, importing, deleting, failure ->
        UiState(
            isLoading = false,
            models = library.models,
            activeModelId = library.activeModelId,
            backends = backends,
            activeBackend = library.activeBackend,
            isImporting = importing,
            pendingDelete = deleting,
            error = failure,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState(),
    )

    fun onEvent(event: Event) {
        when (event) {
            Event.OnNavigateBack -> navigator.navigateUp()

            is Event.OnImport -> import(event.uri)

            is Event.OnSelect -> select(event.model)

            is Event.OnBackendSelected -> selectBackend(event.backend)

            is Event.OnDeleteRequest -> pendingDelete.value = event.model

            Event.OnDeleteConfirm -> confirmDelete()

            Event.OnDeleteCancel -> pendingDelete.value = null

            Event.OnErrorShown -> error.value = null
        }
    }

    private fun import(uri: Uri) {
        if (isImporting.value) return
        viewModelScope.launch {
            isImporting.value = true
            aiRepository.importModel(uri)
                .onSuccess { model -> activate(model) }
                .onFailure { failure -> error.value = failure.asAiError() }
            isImporting.value = false
        }
    }

    private fun select(model: AiModel) {
        viewModelScope.launch {
            activate(model)
        }
    }

    private fun confirmDelete() {
        val model = pendingDelete.value ?: return
        pendingDelete.value = null
        viewModelScope.launch {
            aiRepository.deleteModel(model.id)
                .onFailure { failure -> error.value = failure.asAiError() }
        }
    }

    private suspend fun activate(model: AiModel) {
        val config = aiRepository.config.first()
        aiRepository.configure(
            config?.copy(modelPath = model.path) ?: AiModelConfig(modelPath = model.path)
        ).onFailure { failure -> error.value = failure.asAiError() }
    }

    private fun selectBackend(backend: AiBackend) {
        viewModelScope.launch {
            val config = aiRepository.config.first() ?: return@launch
            if (config.backend == backend) return@launch
            aiRepository.configure(config.copy(backend = backend))
                .onFailure { failure -> error.value = failure.asAiError() }
        }
    }

    private fun supportsGoogleTensor(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            Build.SOC_MODEL.trim().startsWith("Tensor", ignoreCase = true)

    private fun Throwable.asAiError(): AiError = this as? AiError ?: AiError.Unexpected(this)

    private data class Library(
        val models: List<AiModel>,
        val activeModelId: String?,
        val activeBackend: AiBackend,
    )
}
