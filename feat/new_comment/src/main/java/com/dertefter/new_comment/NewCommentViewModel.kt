package com.dertefter.new_comment

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.comments.NewCommentRequestDto
import com.dertefter.navigation.Navigator
import com.dertefter.new_comment.presentation.Event
import com.dertefter.new_comment.presentation.UiState
import com.dertefter.new_comment.presentation.Upload
import com.dertefter.new_comment.presentation.UploadStatus
import com.dertefter.design.components.post.SpanUiModel
import com.dertefter.data.dto.feed.SpanDto
import com.dertefter.data.repository.CommentsRepository
import com.dertefter.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class NewCommentViewModel @Inject constructor(
    private val application: Application,
    private val commentsRepository: CommentsRepository,
    private val navigator: Navigator
) : ViewModel() {


    private val _spans = MutableStateFlow<List<SpanUiModel>>(emptyList())

    private val _uploads = MutableStateFlow<List<Upload>>(emptyList())

    private val _content = MutableStateFlow("")

    private val _isUploadingComment = MutableStateFlow(false)

    private var postId: String? = null

    private var commentId: String? = null

    private var replyToUserId: String? = null

    val uiState: StateFlow<UiState> = combine(_content, _spans, _uploads, _isUploadingComment) { content, spans, uploads, isUploadingPost ->
        UiState(content, spans, uploads, isUploadingPost)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState(
        "",
        emptyList(),
        emptyList()
    ))

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

            Event.OnSaveComment -> {
                postId?.let{ postId ->
                    if (replyToUserId != null && commentId != null){
                        saveCommentReply(
                            postId = postId,
                            commentId = commentId!!,
                            replyToUserId = replyToUserId!!
                        )
                    } else {
                        saveComment(postId)
                    }

                }
            }
        }
    }

    fun clearAll() {
        _content.value = ""
        _spans.value = emptyList()
        _uploads.value = emptyList()
        _isUploadingComment.value = false
    }

    private fun saveComment(postId: String) {
        viewModelScope.launch {
            _isUploadingComment.value = true
            val attachmentIds = _uploads.value
                .filter { it.uploadStatus == UploadStatus.SUCCESS }
                .mapNotNull { it.attachment?.id }

            val request = NewCommentRequestDto(
                content = _content.value,
                spans = _spans.value.map { SpanDto(it.type, it.length, it.offset, it.username, it.tag) },
                attachmentIds = attachmentIds
            )

            val result = commentsRepository.newComment(postId, request)
            if (result.isSuccess) {
                clearAll()
                navigator.openAsBottomSheet(Routes.Comments(postId))
            }
            _isUploadingComment.value = false
        }
    }

    private fun saveCommentReply(postId: String, commentId: String, replyToUserId: String) {
        viewModelScope.launch {
            _isUploadingComment.value = true
            val attachmentIds = _uploads.value
                .filter { it.uploadStatus == UploadStatus.SUCCESS }
                .mapNotNull { it.attachment?.id }

            val request = NewCommentRequestDto(
                content = _content.value,
                spans = _spans.value.map { SpanDto(it.type, it.length, it.offset, it.username, it.tag) },
                attachmentIds = attachmentIds,
                replyToUserId = replyToUserId
            )

            val result = commentsRepository.newCommentReply(commentId, request)
            if (result.isSuccess) {
                clearAll()
                navigator.openAsBottomSheet(Routes.Comments(postId))
            }
            _isUploadingComment.value = false
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
                val result = commentsRepository.upload(file)
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

    fun initCommentForPost(postId: String) {
        this.postId = postId
        this.commentId = null
        this.replyToUserId = null
        clearAll()
    }

    fun initCommentForReply(postId: String, commentId: String, replyToUserId: String) {
        this.postId = postId
        this.commentId = commentId
        this.replyToUserId = replyToUserId
        clearAll()
    }

}
