package com.dertefter.new_post

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.comments.NewCommentRequestDto
import com.dertefter.data.dto.draft.DraftDto
import com.dertefter.data.dto.feed.SpanDto
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import com.dertefter.data.repository.AttachmentsRepository
import com.dertefter.data.repository.CommentsRepository
import com.dertefter.design.components.post.SpanUiModel
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.new_post.presentation.Event
import com.dertefter.new_post.presentation.Upload
import com.dertefter.new_post.presentation.UploadStatus
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class NewCommentViewModel(
    application: Application,
    private val commentsRepository: CommentsRepository,
    attachmentsRepository: AttachmentsRepository,
    navigator: Navigator
) : BaseNewPostViewModel(application, attachmentsRepository, navigator) {

    private var postId: String? = null
    private var commentId: String? = null
    private var replyToUserId: String? = null
    private var currentDraftKey: String? = null
    private var savedDraft: DraftDto? = null
    private var isPostSaved = false

    fun initCommentForPost(postId: String) {
        this.postId = postId
        this.commentId = null
        this.replyToUserId = null
        isPostSaved = false
        clearAll()
        val key = "comment_$postId"
        currentDraftKey = key
        viewModelScope.launch {
            commentsRepository.getCommentDraft(key).collectLatest { draft ->
                savedDraft = draft
                val isNonEmpty = draft.content.isNotBlank() ||
                        draft.spans.isNotEmpty() ||
                        draft.attachmentIds.isNotEmpty()
                hasDraft.value = isNonEmpty
            }
        }
    }

    fun initCommentForReply(postId: String, commentId: String, replyToUserId: String) {
        this.postId = postId
        this.commentId = commentId
        this.replyToUserId = replyToUserId
        isPostSaved = false
        clearAll()
        val key = "comment_reply_$commentId"
        currentDraftKey = key
        viewModelScope.launch {
            commentsRepository.getCommentDraft(key).collectLatest { draft ->
                savedDraft = draft
                val isNonEmpty = draft.content.isNotBlank() ||
                        draft.spans.isNotEmpty() ||
                        draft.attachmentIds.isNotEmpty()
                hasDraft.value = isNonEmpty
            }
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnRestoreDraft -> restoreDraft()
            is Event.OnDeleteDraft -> deleteDraft()
            else -> super.onEvent(event)
        }
    }

    private fun restoreDraft() {
        val draft = savedDraft ?: return
        content.value = draft.content
        spans.value = draft.spans.map {
            SpanUiModel(
                type = it.type,
                length = it.length,
                offset = it.offset,
                username = it.username,
                tag = it.tag,
                url = it.url
            )
        }
        if (draft.attachmentIds.isNotEmpty()) {
            uploads.value = draft.attachmentIds.map { id ->
                Upload(
                    uploadStatus = UploadStatus.SUCCESS,
                    uri = Uri.EMPTY,
                    mimeType = null,
                    attachment = AttachmentUploadResponseDto(id = id)
                )
            }
        }
        hasDraft.value = false
    }

    private fun deleteDraft() {
        hasDraft.value = false
        savedDraft = null
        val key = currentDraftKey ?: return
        viewModelScope.launch(Dispatchers.IO) {
            commentsRepository.saveCommentDraft(DraftDto(postId = postId, commentId = commentId, replyToUserId = replyToUserId), key)
        }
    }

    fun saveDraft() {
        if (isPostSaved) return
        val key = currentDraftKey ?: return
        val attachmentIds = uploads.value
            .filter { it.uploadStatus == UploadStatus.SUCCESS }
            .mapNotNull { it.attachment?.id }

        val request = DraftDto(
            content = content.value,
            spans = spans.value.map {
                SpanDto(
                    it.type,
                    it.length,
                    it.offset,
                    it.username,
                    it.tag,
                    it.url
                )
            },
            attachmentIds = attachmentIds,
            postId = postId,
            commentId = commentId,
            replyToUserId = replyToUserId
        )

        viewModelScope.launch(Dispatchers.IO) {
            commentsRepository.saveCommentDraft(request, key)
        }
    }

    override fun savePost() {
        postId?.let { postId ->
            if (replyToUserId != null && commentId != null) {
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

    private fun saveComment(postId: String) {
        viewModelScope.launch {
            isUploadingPost.value = true
            val attachmentIds = uploads.value
                .filter { it.uploadStatus == UploadStatus.SUCCESS }
                .mapNotNull { it.attachment?.id }

            val request = NewCommentRequestDto(
                content = content.value,
                spans = spans.value.map {
                    SpanDto(
                        it.type,
                        it.length,
                        it.offset,
                        it.username,
                        it.tag,
                        it.url
                    )
                },
                attachmentIds = attachmentIds
            )

            val result = commentsRepository.newComment(postId, request)
            if (result.isSuccess) {
                isPostSaved = true
                currentDraftKey?.let { key ->
                    commentsRepository.saveCommentDraft(DraftDto(postId = postId), key)
                }
                clearAll()
                navigator.openAsBottomSheet(Routes.Comments(postId))
            }
            isUploadingPost.value = false
        }
    }

    private fun saveCommentReply(postId: String, commentId: String, replyToUserId: String) {
        viewModelScope.launch {
            isUploadingPost.value = true
            val attachmentIds = uploads.value
                .filter { it.uploadStatus == UploadStatus.SUCCESS }
                .mapNotNull { it.attachment?.id }

            val request = NewCommentRequestDto(
                content = content.value,
                spans = spans.value.map {
                    SpanDto(
                        it.type,
                        it.length,
                        it.offset,
                        it.username,
                        it.tag,
                        it.url
                    )
                },
                attachmentIds = attachmentIds,
                replyToUserId = replyToUserId
            )

            val result = commentsRepository.newCommentReply(commentId, request)
            if (result.isSuccess) {
                isPostSaved = true
                currentDraftKey?.let { key ->
                    commentsRepository.saveCommentDraft(DraftDto(postId = postId, commentId = commentId, replyToUserId = replyToUserId), key)
                }
                clearAll()
                navigator.openAsBottomSheet(Routes.Comments(postId))
            }
            isUploadingPost.value = false
        }
    }

    override fun onCleared() {
        saveDraft()
    }
}
