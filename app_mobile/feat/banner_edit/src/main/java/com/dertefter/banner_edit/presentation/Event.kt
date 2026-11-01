package com.dertefter.banner_edit.presentation

import android.net.Uri
import com.dertefter.banner_edit.presentation.drawbox.BannerStroke

sealed interface Event {
    data class OnSaveDrawing(val strokes: List<BannerStroke>) : Event
    object OnSave : Event
    object OnBack : Event
    data class OnImportImage(val uri: Uri) : Event
    data class OnSelectImportMode(val mode: ImportMode) : Event
    object OnCancelImport : Event
    data class OnAcceptImport(val strokes: List<BannerStroke>) : Event
    object OnRejectImport : Event
}
