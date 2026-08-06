package com.dertefter.new_post

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.feed.SpanDto
import com.dertefter.data.dto.new_post.EditPostRequestDto
import com.dertefter.data.repository.AttachmentsRepository
import com.dertefter.data.repository.PostRepository
import com.dertefter.design.components.post.SpanUiModel
import com.dertefter.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    application: Application,
    private val postRepository: PostRepository,
    attachmentsRepository: AttachmentsRepository,
    navigator: Navigator
) : BaseNewPostViewModel(application, attachmentsRepository, navigator) {

    private var postId: String? = null

    fun initWith(postId: String) {
        this.postId = postId
        viewModelScope.launch {
            postRepository.getPost(postId).collectLatest { post ->
                if (post != null) {
                    _content.value = post.content
                    _spans.value = post.spans.map { SpanUiModel(it.type, it.length, it.offset) }
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
            _isUploadingPost.value = true
            val request = EditPostRequestDto(
                content = _content.value,
                spans = _spans.value.map { SpanDto(it.type, it.length, it.offset) }
            )

            val result = postRepository.editPost(postId, request)

            if (result.isSuccess) {
                clearAll()
                navigator.hideBottomSheet()
            }
            _isUploadingPost.value = false
        }
    }
}
