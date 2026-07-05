package com.dertefter.banner_edit.presentation

import android.net.Uri

data class UiState(
    val uploadStatus: UploadStatus? = null,
    val uri: Uri? = null,
    val bannerId: String? = null
)