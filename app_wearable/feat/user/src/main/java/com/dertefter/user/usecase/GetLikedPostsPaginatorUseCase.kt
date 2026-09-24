package com.dertefter.user.usecase

import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.repository.FeedRepository
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import javax.inject.Inject

class GetLikedPostsPaginatorUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    operator fun invoke(userId: String): MutableCursorPaginator<String, PostDto> {
        return feedRepository.getLikedPostsPaginator(userId)
    }
}
