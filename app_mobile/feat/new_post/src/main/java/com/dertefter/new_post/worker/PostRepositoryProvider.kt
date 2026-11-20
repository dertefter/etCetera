package com.dertefter.new_post.worker

import com.dertefter.data.repository.PostRepository

interface PostRepositoryProvider {
    val postRepository: PostRepository
}
