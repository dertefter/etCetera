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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPostViewModel @Inject constructor(
    application: Application,
    private val postRepository: PostRepository,
    attachmentsRepository: AttachmentsRepository,
    navigator: Navigator
) : BaseNewPostViewModel(application, attachmentsRepository, navigator) {

    private var wallRecipientId: String? = null

    fun initWith(wallRecipientId: String?) {
        this.wallRecipientId = wallRecipientId
        clearAll()
    }

    override fun savePost() {
        viewModelScope.launch {
            isUploadingPost.value = true
            val pollDto = poll.value?.let { poll ->
                NewPollDto(
                    question = poll.title,
                    options = poll.questions.map { NewPollOptionDto(it.text) },
                    multipleChoice = poll.isMultipleChoice
                )
            }
            val attachmentIds = uploads.value
                .filter { it.uploadStatus == UploadStatus.SUCCESS }
                .mapNotNull { it.attachment?.id }

            val request = NewPostRequestDto(
                content = content.value,
                spans = spans.value.map { SpanDto(it.type, it.length, it.offset, it.username, it.tag, it.url) },
                poll = pollDto,
                attachmentIds = attachmentIds,
                wallRecipientId = wallRecipientId
            )

            val result = postRepository.newPost(request)

            if (result.isSuccess) {
                clearAll()
                navigator.hideBottomSheet()
            }
            isUploadingPost.value = false
        }
    }
}
