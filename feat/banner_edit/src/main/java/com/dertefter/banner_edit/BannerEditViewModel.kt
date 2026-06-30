package com.dertefter.banner_edit

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.banner_edit.presentation.Event
import com.dertefter.banner_edit.presentation.UiState
import com.dertefter.banner_edit.presentation.UploadStatus
import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.repository.AttachmentsRepository
import com.dertefter.data.repository.MeRepository
import com.dertefter.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class BannerEditViewModel @Inject constructor(
    private val application: Application,
    private val attachmentsRepository: AttachmentsRepository,
    private val meRepository: MeRepository,
    private val navigator: Navigator
) : ViewModel() {


    private val _uploadingStatus = MutableStateFlow<UploadStatus?>(null)

    private val _uri = MutableStateFlow<Uri?>(null)

    private val _id = MutableStateFlow<String?>(null)

    val uiState: StateFlow<UiState> = combine(_uploadingStatus, _uri, _id) { uploadingStatus, uri, id ->
        UiState(uploadingStatus, uri, id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnPhotoSelected -> {
                _uri.value = event.uri
            }
            is Event.OnSaveDrawing -> {
                uploadBitmap(event.bitmap)
            }
            Event.OnSave -> {
                saveBannerId(_id.value)
            }
            Event.OnBack -> {
                navigator.navigateUp()
            }
        }
    }

    private fun uploadBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                _uploadingStatus.value = UploadStatus.UPLOADING
                val file = File(application.cacheDir, "banner_drawing.png")
                file.outputStream().use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                attachmentsRepository.upload(file).onFailure {
                    _uploadingStatus.value = UploadStatus.ERROR
                }.onSuccess {
                    _id.value = it.id
                    _uploadingStatus.value = UploadStatus.SUCCESS
                    saveBannerId(it.id)
                }
            } catch (_: Exception) {
                _uploadingStatus.value = UploadStatus.ERROR
            }
        }
    }

    private fun saveBannerId(bannerId: String?) {
        viewModelScope.launch {
            meRepository.updateMe(
                UpdateMeRequestDto(
                    bannerId = bannerId
                )
            ).onSuccess {
                navigator.navigateUp()
            }
        }
    }

}
