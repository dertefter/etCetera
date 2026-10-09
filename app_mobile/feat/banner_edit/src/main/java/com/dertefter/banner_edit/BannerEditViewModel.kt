package com.dertefter.banner_edit

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.banner_edit.presentation.Event
import com.dertefter.banner_edit.presentation.UiState
import com.dertefter.banner_edit.presentation.UploadStatus
import com.dertefter.banner_edit.usecase.GetMeUseCase
import com.dertefter.banner_edit.usecase.NavigateBackUseCase
import com.dertefter.banner_edit.usecase.SaveBannerUseCase
import com.dertefter.banner_edit.usecase.UploadBannerDrawingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BannerEditViewModel @Inject constructor(
    private val uploadBannerDrawingUseCase: UploadBannerDrawingUseCase,
    private val saveBannerUseCase: SaveBannerUseCase,
    private val navigateBackUseCase: NavigateBackUseCase,
    private val getMeUseCase: GetMeUseCase
) : ViewModel() {

    private val _uploadingStatus = MutableStateFlow<UploadStatus?>(null)
    private val _uri = MutableStateFlow<Uri?>(null)
    private val _id = MutableStateFlow<String?>(null)

    val uiState: StateFlow<UiState> = combine(_uploadingStatus, _uri, _id) { uploadingStatus, uri, id ->
        UiState(uploadingStatus, uri, id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())

    init {
        viewModelScope.launch {
            _uploadingStatus.value = UploadStatus.UPLOADING
            getMeUseCase().onSuccess { me ->
                val bannerUrl = me.banner
                if (!bannerUrl.isNullOrEmpty() && _uri.value == null) {
                    _uri.value = bannerUrl.toUri()
                }
            }
            _uploadingStatus.value = null
        }
    }


    fun onEvent(event: Event) {
        when (event) {
            is Event.OnPhotoSelected -> {
                // Handled in UI placement flow
            }
            is Event.OnSaveDrawing -> {
                uploadBitmap(event.bitmap)
            }
            Event.OnSave -> {
                saveBannerId(_id.value)
            }
            Event.OnBack -> {
                navigateBackUseCase()
            }
        }
    }

    private fun uploadBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _uploadingStatus.value = UploadStatus.UPLOADING
            uploadBannerDrawingUseCase(bitmap)
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
