package com.dertefter.data.dto.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDataDto(
    @SerialName("posts") val posts: List<PostDto>,
    @SerialName("pagination") val pagination: PaginationPostsDto
)