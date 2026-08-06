package com.dertefter.new_post

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.comments.NewCommentRequestDto
import com.dertefter.data.dto.feed.SpanDto
import com.dertefter.data.repository.AttachmentsRepository
import com.dertefter.data.repository.CommentsRepository
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.new_post.presentation.UploadStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewCommentViewModel @Inject constructor(
    application: Application,
    private val commentsRepository: CommentsRepository,
    attachmentsRepository: AttachmentsRepository,
    navigator: Navigator
) : BaseNewPostViewModel(application, attachmentsRepository, navigator) {

    private var postId: String? = null
    private var commentId: String? = null
    private var replyToUserId: String? = null

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
            _isUploadingPost.value = true
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
            _isUploadingPost.value = false
        }
    }

    private fun saveCommentReply(postId: String, commentId: String, replyToUserId: String) {
        viewModelScope.launch {
            _isUploadingPost.value = true
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
            _isUploadingPost.value = false
        }
    }
}
