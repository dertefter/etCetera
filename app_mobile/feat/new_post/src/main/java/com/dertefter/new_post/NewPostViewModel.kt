package com.dertefter.new_post

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.repository.AttachmentsRepository
import com.dertefter.data.repository.PostRepository
import com.dertefter.navigation.Navigator
import com.dertefter.new_post.presentation.Event
import com.dertefter.new_post.presentation.UiState
import com.dertefter.new_post.presentation.Upload
import com.dertefter.new_post.presentation.UploadStatus
import com.dertefter.design.components.poll.NewPollUiModel
import com.dertefter.design.components.poll.NewPollOptionUiModel
import com.dertefter.design.components.post.SpanUiModel
import com.dertefter.data.dto.new_post.NewPostRequestDto
import com.dertefter.data.dto.new_post.NewPollDto
import com.dertefter.data.dto.new_post.NewPollOptionDto
import com.dertefter.data.dto.feed.SpanDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewPostViewModel @Inject constructor(
    private val application: Application,
    private val postRepository: PostRepository,
    private val attachmentsRepository: AttachmentsRepository,
    private val navigator: Navigator
) : ViewModel() {


    private val _poll = MutableStateFlow<NewPollUiModel?>(null)

    private val _spans = MutableStateFlow<List<SpanUiModel>>(emptyList())

    private val _uploads = MutableStateFlow<List<Upload>>(emptyList())

    private val _content = MutableStateFlow("")

    private val _isUploadingPost = MutableStateFlow(false)

    private var wallRecipientId: String? = null

    private val _postIdForRepost = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _originalPost = _postIdForRepost.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else postRepository.getPost(id)
    }

    @Suppress("UNCHECKED_CAST")
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

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnPhotosSelected -> {
                uploadPhotos(event.uris)
            }

            is Event.OnRemoveUpload -> {
                _uploads.update { it.filter { upload -> upload.uri != event.uri } }
            }

            is Event.OnRetryUpload -> {
                retryUpload(event.uri)
            }

            is Event.OnContentChanged -> {
                _content.value = event.content
            }

            is Event.OnSpanToggled -> {
                val type = event.type
                val start = event.start
                val end = event.end
                val length = end - start
                _spans.update { spans ->
                    val existing = spans.find { it.type == type && it.offset == start && it.length == length }
                    if (existing != null) {
                        spans - existing
                    } else {
                        spans + SpanUiModel(type, length, start)
                    }
                }
            }

            Event.OnAddPoll -> {
                _poll.value = NewPollUiModel(
                    title = "",
                    questions = listOf(
                        NewPollOptionUiModel("", UUID.randomUUID().toString()),
                        NewPollOptionUiModel("", UUID.randomUUID().toString())
                    )
                )
            }

            Event.OnRemovePoll -> {
                _poll.value = null
            }

            is Event.OnPollTitleChanged -> {
                _poll.update { it?.copy(title = event.title) }
            }

            is Event.OnPollQuestionChanged -> {
                _poll.update { poll ->
                    poll?.copy(
                        questions = poll.questions.map {
                            if (it.id == event.id) it.copy(text = event.text) else it
                        }
                    )
                }
            }

            Event.OnAddPollQuestion -> {
                _poll.update { poll ->
                    poll?.copy(
                        questions = poll.questions + NewPollOptionUiModel("", UUID.randomUUID().toString())
                    )
                }
            }

            is Event.OnRemovePollQuestion -> {
                _poll.update { poll ->
                    if (poll == null) return@update null
                    val newQuestions = poll.questions.filter { it.id != event.id }
                    if (newQuestions.size < 2) {
                        null
                    } else {
                        poll.copy(questions = newQuestions)
                    }
                }
            }

            is Event.OnPollMultipleChoiceChanged -> {
                _poll.update { it?.copy(isMultipleChoice = event.isMultipleChoice) }
            }

            Event.OnSavePost -> {
                savePost(wallRecipientId)

            }
        }
    }

    fun clearAll() {
        _content.value = ""
        _spans.value = emptyList()
        _uploads.value = emptyList()
        _poll.value = null
        _isUploadingPost.value = false
    }

    private fun savePost(wallRecipientId: String?) {
        viewModelScope.launch {
            _isUploadingPost.value = true
            val pollDto = _poll.value?.let { poll ->
                NewPollDto(
                    question = poll.title,
                    options = poll.questions.map { NewPollOptionDto(it.text) },
                    multipleChoice = poll.isMultipleChoice
                )
            }
            val attachmentIds = _uploads.value
                .filter { it.uploadStatus == UploadStatus.SUCCESS }
                .mapNotNull { it.attachment?.id }

            val request = NewPostRequestDto(
                content = _content.value,
                spans = _spans.value.map { SpanDto(it.type, it.length, it.offset, it.username, it.tag) },
                poll = pollDto,
                attachmentIds = attachmentIds,
                wallRecipientId = wallRecipientId
            )

            val result = _postIdForRepost.value?.let {
                postRepository.repost(it, request)
            } ?: postRepository.newPost(request)

            if (result.isSuccess) {
                clearAll()
                navigator.hideBottomSheet()
            }
            _isUploadingPost.value = false
        }
    }

    private fun uploadPhotos(uris: List<Uri>) {
        uris.forEach { uri ->
            if (_uploads.value.any { it.uri == uri }) return@forEach
            val newUpload = Upload(UploadStatus.UPLOADING, uri, null)
            _uploads.update { it + newUpload }
            uploadFile(newUpload)
        }
    }

    private fun retryUpload(uri: Uri) {
        _uploads.update { uploads ->
            uploads.map {
                if (it.uri == uri) it.copy(uploadStatus = UploadStatus.UPLOADING) else it
            }
        }
        val upload = _uploads.value.find { it.uri == uri }
        if (upload != null) {
            uploadFile(upload)
        }
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
                    uploads.map {
                        if (it.uri == upload.uri) it.copy(uploadStatus = UploadStatus.ERROR) else it
                    }
                }
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = application.contentResolver.openInputStream(uri)
        val file = File(application.cacheDir, "temp_upload_${System.currentTimeMillis()}.jpg")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    fun initWith(wallRecipientId: String?, postIdForRepost: String?) {
        this.wallRecipientId = wallRecipientId
        this._postIdForRepost.value = postIdForRepost
        postIdForRepost?.let { postIdForRepost ->
            viewModelScope.launch {
                postRepository.updatePost(postIdForRepost)
            }
        }
        clearAll()
    }

}
