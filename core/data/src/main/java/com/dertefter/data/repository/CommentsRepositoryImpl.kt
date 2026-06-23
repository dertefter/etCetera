package com.dertefter.data.repository

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.CommentPagingCache
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.comments.NewCommentRequestDto
import com.dertefter.data.dto.comments.RepliesDataDto
import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cache.eviction.CursorMostRecentPagingCache
import com.jamal_aliev.paginator.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.extension.updateWhere
import com.jamal_aliev.paginator.load.CursorLoadResult
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentsRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : CommentsRepository {

    private val activePaginators = CopyOnWriteArrayList<WeakReference<MutableCursorPaginator<CommentDto>>>()

    private suspend fun updateInMem(action: suspend (MutableCursorPaginator<CommentDto>) -> Unit) {
        activePaginators.removeIf { it.get() == null }
        activePaginators.forEach {
            it.get()?.let { paginator ->
                action(paginator)
                paginator.flush()
            }
        }
    }

    private fun CommentDto.containsComment(targetId: String): Boolean {
        if (this.id == targetId) return true
        return (replies?: emptyList()).any { it.containsComment(targetId) }
    }

    private fun CommentDto.recursiveTransform(targetId: String, transform: (CommentDto) -> CommentDto): CommentDto {
        if (this.id == targetId) return transform(this)
        val updatedReplies = replies?.map { it.recursiveTransform(targetId, transform) }
        return if (updatedReplies != replies) {
            copy(replies = updatedReplies)
        } else {
            this
        }
    }

    private suspend fun updatePagesInDb(commentId: String, transform: (CommentDto) -> CommentDto) {
        val allComments = localDataSource.getAllComments()
        allComments.forEach { comment ->
            if (comment.id == commentId) {
                localDataSource.saveComment(transform(comment))
            }
        }
    }

    private suspend fun updateData(commentId: String, transform: (CommentDto) -> CommentDto) {
        updateInMem {
            it.updateWhere(
                predicate = { item -> item.containsComment(commentId) },
                transform = { item -> item.recursiveTransform(commentId, transform) }
            )
        }
        updatePagesInDb(commentId, transform)
    }

    override fun getCommentsPaginator(postId: String, sort: String): MutableCursorPaginator<CommentDto> {
        val cacheKey = "$postId:$sort"
        return mutableCursorPaginator(capacity = 100) {
            cache = CursorMostRecentPagingCache(maxSize = 100)
            persistentCache = CommentPagingCache(cacheKey, localDataSource)

            load { cursor ->
                val requestCursor = if (cursor?.self == "initial") null else cursor?.self as? String
                val result = remoteDataSource.getComments(postId, requestCursor, sort)
                val data = result.getOrThrow()

                val otherPagesItems = core.cursors
                    .filter { it.self != cursor?.self }
                    .mapNotNull { core.getStateOf(it.self) }
                    .flatMap { it.data }
                val existingIds = otherPagesItems.map { it.id }.toSet()

                val seenInNewPage = mutableSetOf<String>()
                val uniqueComments = data.comments.filter { comment ->
                    val isUnique = comment.id !in existingIds && comment.id !in seenInNewPage
                    if (isUnique) seenInNewPage.add(comment.id)
                    isUnique
                }
                CursorLoadResult(
                    data = uniqueComments,
                    bookmark = CursorBookmark(
                        prev = cursor?.prev,
                        self = cursor?.self ?: "initial",
                        next = if (data.hasMore) data.nextCursor else null
                    )
                )
            }

            initialCursor = CursorBookmark(prev = null, self = "initial", next = null)
        }.also {
            activePaginators.add(WeakReference(it))
        }
    }

    override suspend fun likeComment(commentId: String): Result<LikeResponseDto> {
        applyOptimisticLike(commentId, true)
        return remoteDataSource.likeComment(commentId).onSuccess { response ->
            handleLikeResponse(commentId, response)
        }.onFailure {
            applyOptimisticLike(commentId, false)
        }
    }

    override suspend fun unlikeComment(commentId: String): Result<LikeResponseDto> {
        applyOptimisticLike(commentId, false)
        return remoteDataSource.unlikeComment(commentId).onSuccess { response ->
            handleLikeResponse(commentId, response)
        }.onFailure {
            applyOptimisticLike(commentId, true)
        }
    }

    override suspend fun getReplies(commentId: String, page: String?): Result<RepliesDataDto> {
        return remoteDataSource.getReplies(commentId, page).onSuccess { response ->
            val newReplies = response.replies
            val transform: (CommentDto) -> CommentDto = { comment ->
                val currentReplies = comment.replies ?: emptyList()
                val currentIds = currentReplies.map { it.id }.toSet()
                val filteredNewReplies = newReplies.filter { it.id !in currentIds }
                comment.copy(replies = currentReplies + filteredNewReplies)
            }
            updateData(commentId, transform)
        }
    }

    override suspend fun newComment(
        postId: String,
        newCommentRequestDto: NewCommentRequestDto
    ): Result<CommentDto> {
        return remoteDataSource.newComment(postId, newCommentRequestDto)
    }

    override suspend fun upload(file: File): Result<AttachmentUploadResponseDto> {
        return remoteDataSource.uploadMyFile(file)
    }

    private suspend fun applyOptimisticLike(commentId: String, liked: Boolean) {
        val transform: (CommentDto) -> CommentDto = { comment ->
            if (comment.isLiked == liked) comment
            else {
                val newCount = if (liked) comment.likesCount + 1 else (comment.likesCount - 1).coerceAtLeast(0)
                comment.copy(isLiked = liked, likesCount = newCount)
            }
        }
        updateData(commentId, transform)
    }

    private suspend fun handleLikeResponse(commentId: String, response: LikeResponseDto) {
        val transform: (CommentDto) -> CommentDto = { it.copy(isLiked = response.liked, likesCount = response.likesCount) }
        updateData(commentId, transform)
    }
}
