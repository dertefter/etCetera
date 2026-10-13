package com.dertefter.comments.usecase

import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.repository.CommentsRepository
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import dev.zacsweers.metro.Inject

class GetCommentsPaginatorUseCase @Inject constructor(
    private val commentsRepository: CommentsRepository
) {
    operator fun invoke(postId: String, sort: String): MutableCursorPaginator<String, CommentDto> {
        return commentsRepository.getCommentsPaginator(postId, sort)
    }
}
