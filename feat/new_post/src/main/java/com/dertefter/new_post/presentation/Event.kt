package com.dertefter.new_post.presentation

import android.net.Uri

sealed interface Event {
    data class OnPhotosSelected(val uris: List<Uri>) : Event

    data class OnRemoveUpload(val uri: Uri) : Event

    data class OnRetryUpload(val uri: Uri) : Event

    data class OnContentChanged(val content: String) : Event

    data class OnSpanToggled(val type: String, val start: Int, val end: Int) : Event

    data object OnAddPoll : Event

    data object OnRemovePoll : Event

    data class OnPollTitleChanged(val title: String) : Event

    data class OnPollQuestionChanged(val id: String, val text: String) : Event

    data object OnAddPollQuestion : Event

    data class OnRemovePollQuestion(val id: String) : Event

    data class OnPollMultipleChoiceChanged(val isMultipleChoice: Boolean) : Event

    data object OnSavePost : Event

}
