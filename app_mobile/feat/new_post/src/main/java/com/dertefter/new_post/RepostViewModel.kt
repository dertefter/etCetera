package com.dertefter.new_post

import android.app.Application
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import com.dertefter.data.dto.feed.SpanDto
import com.dertefter.data.dto.new_post.NewPollDto
import com.dertefter.data.dto.new_post.NewPollOptionDto
import com.dertefter.data.dto.new_post.NewPostRequestDto
import com.dertefter.data.repository.AttachmentsRepository
import com.dertefter.data.repository.PostRepository
import com.dertefter.navigation.Navigator
import com.dertefter.new_post.presentation.UploadStatus
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@ViewModelKey(RepostViewModel::class)
@ContributesIntoMap(AppScope::class)
class RepostViewModel @Inject constructor(
    application: Application,
    private val postRepository: PostRepository,
    attachmentsRepository: AttachmentsRepository,
    navigator: Navigator
) : BaseNewPostViewModel(application, attachmentsRepository, navigator) {

    private var postIdForRepost: String? = null
    private var wallRecipientId: String? = null

    fun initWith(postIdForRepost: String, wallRecipientId: String?) {
        this.postIdForRepost = postIdForRepost
        this.wallRecipientId = wallRecipientId
        viewModelScope.launch {
            postRepository.getPost(postIdForRepost).collectLatest {
                originalPost.value = it
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
                poll = pollDto,
                attachmentIds = attachmentIds,
                wallRecipientId = wallRecipientId
            )

            val result = postRepository.repost(postId, request)

            if (result.isSuccess) {
                clearAll()
                navigator.hideBottomSheet()
            }
            isUploadingPost.value = false
        }
    }
}
