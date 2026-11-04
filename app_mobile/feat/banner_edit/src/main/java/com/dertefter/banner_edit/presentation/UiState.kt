package com.dertefter.banner_edit.presentation

import android.net.Uri
import com.dertefter.banner_edit.presentation.drawbox.BannerStroke

data class UiState(
    val uploadStatus: UploadStatus? = null,
    val bannerId: String? = null,
    val importStage: String? = null,
    val importProgress: Float? = null,
    val importedStrokes: List<BannerStroke>? = null,
    val importFailed: Boolean = false,
    val importMode: ImportMode = ImportMode.STROKES,
    val pendingCropUri: Uri? = null,
)
