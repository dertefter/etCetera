package com.dertefter.new_comment.presentation

import android.net.Uri

sealed interface Event {
    data class OnPhotosSelected(val uris: List<Uri>) : Event

    data class OnRemoveUpload(val uri: Uri) : Event

    data class OnRetryUpload(val uri: Uri) : Event

    data class OnContentChanged(val content: String) : Event

    data class OnSpanToggled(val type: String, val start: Int, val end: Int) : Event

    data object OnSaveComment : Event

}
