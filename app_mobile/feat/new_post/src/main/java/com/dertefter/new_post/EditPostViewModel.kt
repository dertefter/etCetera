package com.dertefter.new_post

import android.app.Application
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import com.dertefter.ai.repository.AiRepository
import com.dertefter.data.dto.feed.SpanDto
import com.dertefter.data.dto.new_post.EditPostRequestDto
import com.dertefter.data.repository.AttachmentsRepository
import com.dertefter.data.repository.PostRepository
import com.dertefter.design.components.post.SpanUiModel
import com.dertefter.navigation.Navigator
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class EditPostViewModel(
    application: Application,
    private val postRepository: PostRepository,
    attachmentsRepository: AttachmentsRepository,
    aiRepository: AiRepository,
    navigator: Navigator
) : BaseNewPostViewModel(application, attachmentsRepository, navigator, aiRepository) {

    private var postId: String? = null
    private var isInitialized = false

    fun initWith(postId: String) {
        if (this.postId == postId && isInitialized) return
        this.postId = postId
        isInitialized = false
        viewModelScope.launch {
            postRepository.getPost(postId).collectLatest { post ->
                if (post != null && !isInitialized) {
                    content.value = post.content
                    spans.value = post.spans.map {
                        SpanUiModel(
                            it.type,
                            it.length,
                            it.offset,
                            it.username,
                            it.tag,
                            it.url
                        )
                    }
                    isInitialized = true
                }
            }
        }
        viewModelScope.launch {
            postRepository.updatePost(postId)
        }
    }

    override fun savePost() {
        val postId = postId ?: return
        viewModelScope.launch {
            isUploadingPost.value = true
            val request = EditPostRequestDto(
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

            val result = postRepository.editPost(postId, request)

            if (result.isSuccess) {
                clearAll()
                navigator.hideBottomSheet()
            }
            isUploadingPost.value = false
        }
    }
}
