package com.dertefter.user.usecase

import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.repository.FeedRepository
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import dev.zacsweers.metro.Inject

@Inject
class GetPostsPaginatorUseCase(
    private val feedRepository: FeedRepository
) {
    operator fun invoke(userId: String, pinnedPostId: () -> String?): MutableCursorPaginator<String, PostDto> {
        return feedRepository.getPostsPaginator(userId, pinnedPostId = pinnedPostId)
    }
}
