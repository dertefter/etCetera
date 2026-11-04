package com.dertefter.banner_edit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.banner_edit.presentation.CropRect
import com.dertefter.banner_edit.presentation.Event
import com.dertefter.banner_edit.presentation.ImportMode
import com.dertefter.banner_edit.presentation.UiState
import com.dertefter.banner_edit.presentation.UploadStatus
import com.dertefter.banner_edit.presentation.drawbox.BannerStroke
import com.dertefter.banner_edit.usecase.ImportBannerImageUseCase
import com.dertefter.banner_edit.usecase.NavigateBackUseCase
import com.dertefter.banner_edit.usecase.SaveBannerUseCase
import com.dertefter.banner_edit.usecase.UploadBannerDrawingUseCase

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class BannerEditViewModel(
    private val uploadBannerDrawingUseCase: UploadBannerDrawingUseCase,
    private val saveBannerUseCase: SaveBannerUseCase,
    private val importBannerImageUseCase: ImportBannerImageUseCase,
    private val navigateBackUseCase: NavigateBackUseCase
) : ViewModel() {

    private val _uploadingStatus = MutableStateFlow<UploadStatus?>(null)
    private val _id = MutableStateFlow<String?>(null)
    private val _importStage = MutableStateFlow<String?>(null)
    private val _importProgress = MutableStateFlow<Float?>(null)
    private val _importedStrokes = MutableStateFlow<List<BannerStroke>?>(null)
    private val _importFailed = MutableStateFlow(false)
    private val _importMode = MutableStateFlow(ImportMode.STROKES)
    private val _pendingCropUri = MutableStateFlow<Uri?>(null)
    private var importJob: Job? = null

    val uiState: StateFlow<UiState> = combine(
        _uploadingStatus,
        _id,
        combine(_importStage, _importProgress) { stage, progress -> stage to progress },
        combine(_importedStrokes, _importMode) { imported, mode -> imported to mode },
        combine(_importFailed, _pendingCropUri) { failed, cropUri -> failed to cropUri },
    ) { uploadingStatus, id, (stage, progress), (imported, mode), (failed, cropUri) ->
        UiState(uploadingStatus, id, stage, progress, imported, failed, mode, cropUri)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnSaveDrawing -> {
                uploadDrawing(event.strokes)
            }
            Event.OnSave -> {
                saveBannerId(_id.value)
            }
            Event.OnBack -> {
                navigateBackUseCase()
            }
            is Event.OnImagePicked -> {
                _pendingCropUri.value = event.uri
            }
            is Event.OnCropConfirmed -> {
                _pendingCropUri.value = null
                importImage(event.uri, event.cropRect)
            }
            Event.OnCropCancelled -> {
                _pendingCropUri.value = null
            }
            is Event.OnImportImage -> {
                importImage(event.uri, event.cropRect)
            }
            is Event.OnSelectImportMode -> {
                _importMode.value = event.mode
            }
            Event.OnCancelImport -> {
                importJob?.cancel()
                importJob = null
                _importStage.value = null
                _importProgress.value = null
            }
            is Event.OnAcceptImport -> {
                _importedStrokes.value = null
            }
            Event.OnRejectImport -> {
                _importedStrokes.value = null
            }
        }
    }

    private fun importImage(uri: Uri, cropRect: CropRect?) {
        if (importJob?.isActive == true) return
        _importFailed.value = false
        _importStage.value = ""
        _importProgress.value = 0f
        importJob = viewModelScope.launch {
            try {
                val result = importBannerImageUseCase(uri, _importMode.value.engine, cropRect = cropRect) { stage, done ->
                    _importStage.value = stage
                    _importProgress.value = done.coerceIn(0.0, 1.0).toFloat()
                }
                _importedStrokes.value = result.strokes
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _importFailed.value = true
            } finally {
                _importStage.value = null
                _importProgress.value = null
            }
        }
    }

    private fun uploadDrawing(strokes: List<BannerStroke>) {
        viewModelScope.launch {
            _uploadingStatus.value = UploadStatus.UPLOADING
            uploadBannerDrawingUseCase(strokes)
                .onFailure {
                    _uploadingStatus.value = UploadStatus.ERROR
                }
                .onSuccess { response ->
                    _id.value = response.id
                    _uploadingStatus.value = UploadStatus.SUCCESS
                    saveBannerId(response.id)
                }
        }
    }

    private fun saveBannerId(bannerId: String?) {
        viewModelScope.launch {
            saveBannerUseCase(bannerId)
                .onSuccess {
                    navigateBackUseCase()
                }
        }
    }
}
