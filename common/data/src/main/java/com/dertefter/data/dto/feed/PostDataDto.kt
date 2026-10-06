package com.dertefter.data.dto.feed

import kotlinx.serialization.Serializable

@Serializable
data class PostDataDto(
    val posts: List<PostDto>,
    val pagination: PaginationPostsDto
)