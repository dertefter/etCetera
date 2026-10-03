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

    private val _poll = MutableStateFlow<NewPollUiModel?>(null)
    protected val poll: MutableStateFlow<NewPollUiModel?> = _poll

    private val _spans = MutableStateFlow<List<SpanUiModel>>(emptyList())
    protected val spans: MutableStateFlow<List<SpanUiModel>> = _spans

    private val _uploads = MutableStateFlow<List<Upload>>(emptyList())
    protected val uploads: MutableStateFlow<List<Upload>> = _uploads

    private val _content = MutableStateFlow("")
    protected val content: MutableStateFlow<String> = _content

    private val _isUploadingPost = MutableStateFlow(false)
    protected val isUploadingPost: MutableStateFlow<Boolean> = _isUploadingPost

    private val _originalPost = MutableStateFlow<PostDto?>(null)
    protected val originalPost: MutableStateFlow<PostDto?> = _originalPost

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
                val oldText = _content.value
                val newText = event.content
                _content.value = newText
                _spans.update { spans ->
                    adjustSpans(spans, oldText, newText)
                }
            }
            is Event.OnSpanToggled -> toggleSpan(event.type, event.start, event.end)
            is Event.OnLinkAdded -> addLinkSpan(event.url, event.start, event.end)
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
        if (length <= 0) return
        _spans.update { spans ->
            val existing = spans.find { it.type == type && it.offset == start && it.length == length }
            if (existing != null) spans - existing else spans + SpanUiModel(type, length, start)
        }
    }

    protected fun adjustSpans(spans: List<SpanUiModel>, oldText: String, newText: String): List<SpanUiModel> {
        if (newText.isEmpty()) return emptyList()
        if (oldText == newText) return spans
        if (oldText.isEmpty()) return spans

        var prefixLen = 0
        val maxPrefix = minOf(oldText.length, newText.length)
        while (prefixLen < maxPrefix && oldText[prefixLen] == newText[prefixLen]) {
            prefixLen++
        }

        var suffixLen = 0
        val maxSuffix = minOf(oldText.length - prefixLen, newText.length - prefixLen)
        while (suffixLen < maxSuffix && oldText[oldText.length - 1 - suffixLen] == newText[newText.length - 1 - suffixLen]) {
            suffixLen++
        }

        val editStart = prefixLen
        val editEndOld = oldText.length - suffixLen
        val editEndNew = newText.length - suffixLen
        val delta = editEndNew - editEndOld

        return spans.mapNotNull { span ->
            val oldStart = span.offset
            val oldEnd = span.offset + span.length

            val newStart: Int
            val newEnd: Int

            if (editStart == editEndOld) {
                // Pure insertion
                if (oldStart < editStart && oldEnd > editStart) {
                    // Insertion inside span -> span expands
                    newStart = oldStart
                    newEnd = oldEnd + delta
                } else if (oldStart >= editStart) {
                    // Insertion before or at span start -> shift span right
                    newStart = oldStart + delta
                    newEnd = oldEnd + delta
                } else {
                    // Insertion after span -> no change
                    newStart = oldStart
                    newEnd = oldEnd
                }
            } else {
                // Deletion or replacement
                if (oldEnd <= editStart) {
                    // Edit is completely after span
                    newStart = oldStart
                    newEnd = oldEnd
                } else if (oldStart >= editEndOld) {
                    // Edit is completely before span
                    newStart = oldStart + delta
                    newEnd = oldEnd + delta
                } else {
                    // Overlap with edit region
                    newStart = if (oldStart < editStart) oldStart else editStart
                    newEnd = if (oldEnd > editEndOld) oldEnd + delta else editStart
                }
            }

            val newLength = newEnd - newStart
            if (newLength > 0 && newStart >= 0 && newStart + newLength <= newText.length) {
                span.copy(offset = newStart, length = newLength)
            } else {
                null
            }
        }
    }

    private fun addLinkSpan(rawUrl: String, start: Int, end: Int) {
        val trimmed = rawUrl.trim()
        if (trimmed.isBlank()) return
        val url = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "https://$trimmed"
        }

        var effectiveStart = start
        var effectiveEnd = end

        if (effectiveStart == effectiveEnd) {
            val currentContent = _content.value
            val safeOffset = effectiveStart.coerceIn(0, currentContent.length)
            val newContent = StringBuilder(currentContent).insert(safeOffset, url).toString()
            _content.value = newContent
            effectiveStart = safeOffset
            effectiveEnd = safeOffset + url.length
        }

        val length = effectiveEnd - effectiveStart
        if (length <= 0) return

        _spans.update { spans ->
            val existing = spans.find { it.type == "link" && it.offset == effectiveStart && it.length == length }
            if (existing != null) {
                spans - existing + SpanUiModel(type = "link", length = length, offset = effectiveStart, url = url)
            } else {
                spans + SpanUiModel(type = "link", length = length, offset = effectiveStart, url = url)
            }
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
