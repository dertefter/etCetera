package com.dertefter.banner_edit.presentation

import android.net.Uri
import com.dertefter.banner_edit.presentation.drawbox.BannerStroke

sealed interface Event {
    data class OnSaveDrawing(val strokes: List<BannerStroke>) : Event
    object OnSave : Event
    object OnBack : Event
    data class OnImagePicked(val uri: Uri) : Event
    data class OnCropConfirmed(val uri: Uri, val cropRect: CropRect) : Event
    object OnCropCancelled : Event
    data class OnImportImage(val uri: Uri, val cropRect: CropRect?) : Event
    data class OnSelectImportMode(val mode: ImportMode) : Event
    object OnCancelImport : Event
    data class OnAcceptImport(val strokes: List<BannerStroke>) : Event
    object OnRejectImport : Event
}
