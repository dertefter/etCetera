package com.dertefter.banner_edit.presentation

import android.graphics.Bitmap
import android.net.Uri

sealed interface Event {
    data class OnPhotoSelected(val uri: Uri) : Event
    data class OnSaveDrawing(val bitmap: Bitmap) : Event
    object OnSave : Event
    object OnBack : Event
}
