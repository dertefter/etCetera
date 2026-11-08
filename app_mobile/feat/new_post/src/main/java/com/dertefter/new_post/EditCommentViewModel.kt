package com.dertefter.new_post

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.ai.repository.AiRepository
import com.dertefter.data.dto.comments.EditCommentRequestDto
import com.dertefter.data.dto.feed.SpanDto
import com.dertefter.data.repository.AttachmentsRepository
import com.dertefter.data.repository.CommentsRepository
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class EditCommentViewModel(
    application: Application,
    private val commentsRepository: CommentsRepository,
    attachmentsRepository: AttachmentsRepository,
    aiRepository: AiRepository,
    navigator: Navigator
) : BaseNewPostViewModel(application, attachmentsRepository, navigator, aiRepository) {

    private var commentId: String? = null
    private var postId: String? = null
    private var isInitialized = false

    fun initWith(postId: String, commentId: String) {
        if (this.commentId == commentId && isInitialized) return
        this.postId = postId
        this.commentId = commentId
        isInitialized = false
        viewModelScope.launch {
            commentsRepository.getComment(commentId).collectLatest { comment ->
                if (comment != null && !isInitialized) {
                    content.value = comment.content
                    isInitialized = true
                }
            }
        }
    }

    override fun savePost() {
        val commentId = commentId ?: return
        val postId = postId ?: return
        viewModelScope.launch {
            isUploadingPost.value = true
            val request = EditCommentRequestDto(
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
                }
            )

            val result = commentsRepository.editComment(commentId, request)

            if (result.isSuccess) {
                clearAll()
                navigator.openAsBottomSheet(Routes.Comments(postId))
            }
            isUploadingPost.value = false
        }
    }
}
