package com.dertefter.new_post

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.dertefter.data.repository.AttachmentsRepository
import com.dertefter.data.repository.PostRepository
import com.dertefter.navigation.Navigator
import com.dertefter.new_post.presentation.UploadStatus
import com.dertefter.data.dto.new_post.NewPostRequestDto
import com.dertefter.data.dto.new_post.NewPollDto
import com.dertefter.data.dto.new_post.NewPollOptionDto
import com.dertefter.data.dto.feed.SpanDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepostViewModel @Inject constructor(
    application: Application,
    postRepository: PostRepository,
    attachmentsRepository: AttachmentsRepository,
    navigator: Navigator
) : BaseNewPostViewModel(application, postRepository, attachmentsRepository, navigator) {

    private var postIdForRepost: String? = null
    private var wallRecipientId: String? = null

    fun initWith(postIdForRepost: String, wallRecipientId: String?) {
        this.postIdForRepost = postIdForRepost
        this.wallRecipientId = wallRecipientId
        viewModelScope.launch {
            postRepository.getPost(postIdForRepost).collectLatest {
                _originalPost.value = it
            }
        }
        viewModelScope.launch {
            postRepository.updatePost(postIdForRepost)
        }
        clearAll()
    }

    override fun savePost() {
        val postId = postIdForRepost ?: return
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

            val result = postRepository.repost(postId, request)

            if (result.isSuccess) {
                clearAll()
                navigator.hideBottomSheet()
            }
            _isUploadingPost.value = false
        }
    }
}
