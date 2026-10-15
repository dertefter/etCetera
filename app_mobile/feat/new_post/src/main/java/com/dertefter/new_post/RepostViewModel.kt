package com.dertefter.new_post

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.draft.DraftDto
import com.dertefter.data.dto.feed.SpanDto
import com.dertefter.data.dto.new_post.NewPollDto
import com.dertefter.data.dto.new_post.NewPollOptionDto
import com.dertefter.data.dto.new_post.NewPostRequestDto
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import com.dertefter.data.repository.AttachmentsRepository
import com.dertefter.data.repository.PostRepository
import com.dertefter.design.components.poll.NewPollOptionUiModel
import com.dertefter.design.components.poll.NewPollUiModel
import com.dertefter.design.components.post.SpanUiModel
import com.dertefter.navigation.Navigator
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
import java.util.UUID


@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class RepostViewModel(
    application: Application,
    private val postRepository: PostRepository,
    attachmentsRepository: AttachmentsRepository,
    navigator: Navigator
) : BaseNewPostViewModel(application, attachmentsRepository, navigator) {

    private var postIdForRepost: String? = null
    private var wallRecipientId: String? = null
    private var savedDraft: DraftDto? = null
    private var isPostSaved = false

    fun initWith(postIdForRepost: String, wallRecipientId: String?) {
        this.postIdForRepost = postIdForRepost
        this.wallRecipientId = wallRecipientId
        isPostSaved = false
        clearAll()
        val draftKey = "repost_${postIdForRepost}_${wallRecipientId ?: "main"}"
        viewModelScope.launch {
            postRepository.getPost(postIdForRepost).collectLatest {
                originalPost.value = it
            }
        }
        viewModelScope.launch {
            postRepository.updatePost(postIdForRepost)
        }
        viewModelScope.launch {
            postRepository.getPostDraft(draftKey).collectLatest { draft ->
                savedDraft = draft
                val isNonEmpty = draft.content.isNotBlank() ||
                        draft.spans.isNotEmpty() ||
                        draft.poll != null ||
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
        poll.value = draft.poll?.let { pollDto ->
            NewPollUiModel(
                title = pollDto.question,
                questions = pollDto.options.map { NewPollOptionUiModel(it.text, UUID.randomUUID().toString()) },
                isMultipleChoice = pollDto.multipleChoice
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
        val postId = postIdForRepost ?: return
        val draftKey = "repost_${postId}_${wallRecipientId ?: "main"}"
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.savePostDraft(DraftDto(wallRecipientId = wallRecipientId, postId = postId), draftKey)
        }
    }

    fun saveDraft() {
        if (isPostSaved) return
        val postId = postIdForRepost ?: return
        val draftKey = "repost_${postId}_${wallRecipientId ?: "main"}"
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
            poll = pollDto,
            attachmentIds = attachmentIds,
            wallRecipientId = wallRecipientId,
            postId = postId
        )

        viewModelScope.launch(Dispatchers.IO) {
            postRepository.savePostDraft(request, draftKey)
        }
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
                isPostSaved = true
                val draftKey = "repost_${postId}_${wallRecipientId ?: "main"}"
                postRepository.savePostDraft(DraftDto(wallRecipientId = wallRecipientId, postId = postId), draftKey)
                clearAll()
                navigator.hideBottomSheet()
            }
            isUploadingPost.value = false
        }
    }

    override fun onCleared() {
        saveDraft()
    }
}
