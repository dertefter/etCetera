package com.dertefter.new_post

import android.app.Application
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.repository.AttachmentsRepository
import com.dertefter.design.components.poll.NewPollOptionUiModel
import com.dertefter.design.components.poll.NewPollUiModel
import com.dertefter.design.components.post.SpanUiModel
import com.dertefter.navigation.Navigator
import com.dertefter.new_post.presentation.Event
import com.dertefter.new_post.presentation.UiState
import com.dertefter.new_post.presentation.Upload
import com.dertefter.new_post.presentation.UploadStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

@Suppress("unchecked_cast")
abstract class BaseNewPostViewModel(
    protected val application: Application,
    protected val attachmentsRepository: AttachmentsRepository,
    protected val navigator: Navigator
) : ViewModel() {

    protected val _poll = MutableStateFlow<NewPollUiModel?>(null)
    protected val _spans = MutableStateFlow<List<SpanUiModel>>(emptyList())
    protected val _uploads = MutableStateFlow<List<Upload>>(emptyList())
    protected val _content = MutableStateFlow("")
    protected val _isUploadingPost = MutableStateFlow(false)
    protected val _originalPost = MutableStateFlow<PostDto?>(null)

    val uiState: StateFlow<UiState> = combine(
        _content,
        _spans,
        _uploads,
        _poll,
        _isUploadingPost,
        _originalPost
    ) { flows ->
        UiState(
            content = flows[0] as String,
            spans = flows[1] as List<SpanUiModel>,
            uploads = flows[2] as List<Upload>,
            poll = flows[3] as NewPollUiModel?,
            isUploadingPost = flows[4] as Boolean,
            originalPost = flows[5] as PostDto?
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), UiState(
            "",
            emptyList(),
            emptyList()
        )
    )

    open fun onEvent(event: Event) {
        when (event) {
            is Event.OnMediaSelected -> uploadMedia(event.uris)
            is Event.OnRemoveUpload -> _uploads.update { it.filter { upload -> upload.uri != event.uri } }
            is Event.OnRetryUpload -> retryUpload(event.uri)
            is Event.OnContentChanged -> {
                _content.value = event.content
                _spans.update { spans ->
                    spans.filter { it.offset + it.length <= event.content.length }
                }
            }
            is Event.OnSpanToggled -> toggleSpan(event.type, event.start, event.end)
            Event.OnAddPoll -> addPoll()
            Event.OnRemovePoll -> _poll.value = null
            is Event.OnPollTitleChanged -> _poll.update { it?.copy(title = event.title) }
            is Event.OnPollQuestionChanged -> updatePollQuestion(event.id, event.text)
            Event.OnAddPollQuestion -> addPollQuestion()
            is Event.OnRemovePollQuestion -> removePollQuestion(event.id)
            is Event.OnPollMultipleChoiceChanged -> _poll.update { it?.copy(isMultipleChoice = event.isMultipleChoice) }
            Event.OnSavePost -> savePost()
        }
    }

    private fun toggleSpan(type: String, start: Int, end: Int) {
        val length = end - start
        _spans.update { spans ->
            val existing = spans.find { it.type == type && it.offset == start && it.length == length }
            if (existing != null) spans - existing else spans + SpanUiModel(type, length, start)
        }
    }

    private fun addPoll() {
        _poll.value = NewPollUiModel(
            title = "",
            questions = listOf(
                NewPollOptionUiModel("", UUID.randomUUID().toString()),
                NewPollOptionUiModel("", UUID.randomUUID().toString())
            )
        )
    }

    private fun updatePollQuestion(id: String, text: String) {
        _poll.update { poll ->
            poll?.copy(questions = poll.questions.map { if (it.id == id) it.copy(text = text) else it })
        }
    }

    private fun addPollQuestion() {
        _poll.update { poll ->
            poll?.copy(questions = poll.questions + NewPollOptionUiModel("", UUID.randomUUID().toString()))
        }
    }

    private fun removePollQuestion(id: String) {
        _poll.update { poll ->
            if (poll == null) return@update null
            val newQuestions = poll.questions.filter { it.id != id }
            if (newQuestions.size < 2) null else poll.copy(questions = newQuestions)
        }
    }

    abstract fun savePost()

    protected fun clearAll() {
        _content.value = ""
        _spans.value = emptyList()
        _uploads.value = emptyList()
        _poll.value = null
        _isUploadingPost.value = false
    }

    protected fun uploadMedia(uris: List<Uri>) {
        uris.forEach { uri ->
            if (_uploads.value.any { it.uri == uri }) return@forEach
            val mimeType = application.contentResolver.getType(uri)
            val newUpload = Upload(UploadStatus.UPLOADING, uri, mimeType, null)
            _uploads.update { it + newUpload }
            uploadFile(newUpload)
        }
    }

    protected fun retryUpload(uri: Uri) {
        _uploads.update { uploads ->
            uploads.map { if (it.uri == uri) it.copy(uploadStatus = UploadStatus.UPLOADING) else it }
        }
        _uploads.value.find { it.uri == uri }?.let { uploadFile(it) }
    }

    private fun uploadFile(upload: Upload) {
        viewModelScope.launch {
            try {
                val file = uriToFile(upload.uri)
                val result = attachmentsRepository.upload(file)
                _uploads.update { uploads ->
                    uploads.map {
                        if (it.uri == upload.uri) {
                            if (result.isSuccess) {
                                it.copy(uploadStatus = UploadStatus.SUCCESS, attachment = result.getOrNull())
                            } else {
                                it.copy(uploadStatus = UploadStatus.ERROR)
                            }
                        } else it
                    }
                }
            } catch (_: Exception) {
                _uploads.update { uploads ->
                    uploads.map { if (it.uri == upload.uri) it.copy(uploadStatus = UploadStatus.ERROR) else it }
                }
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = application.contentResolver.openInputStream(uri)
        val mimeType = application.contentResolver.getType(uri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "bin"
        val file = File(application.cacheDir, "temp_upload_${System.currentTimeMillis()}.$extension")
        inputStream?.use { input -> file.outputStream().use { output -> input.copyTo(output) } }
        return file
    }
}
