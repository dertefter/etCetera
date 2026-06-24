package com.dertefter.data.datasource.local.room

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.entity.PageEntity
import com.dertefter.data.datasource.local.room.entity.PageType
import com.dertefter.data.dto.comments.CommentDto
import com.jamal_aliev.paginator.core.extension.isEmptyState
import com.jamal_aliev.paginator.core.extension.isSuccessState
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.cache.persistent.CursorPersistentPagingCache
import com.jamal_aliev.paginator.cursor.page.CursorPageState

class CommentPagingCache(
    private val cacheKey: String,
    private val localDataSource: LocalDataSource
) : CursorPersistentPagingCache<String, CommentDto> {

    private val type = PageType.COMMENT

    override suspend fun save(cursor: CursorBookmark<String>, state: CursorPageState<String, CommentDto>) {
        if (state.isSuccessState()) {
            val self = cursor.self
            localDataSource.upsertPage(
                PageEntity(
                    type = type,
                    tab = cacheKey,
                    self = self,
                    prev = cursor.prev,
                    next = cursor.next,
                    isEmpty = state.isEmptyState()
                )
            )
            localDataSource.saveComments(type, cacheKey, self, state.data)
        }
    }

    override suspend fun saveAll(entries: List<Pair<CursorBookmark<String>, CursorPageState<String, CommentDto>>>) {
        entries.forEach { (cursor, state) -> save(cursor, state) }
    }

    override suspend fun load(self: String): Pair<CursorBookmark<String>, CursorPageState<String, CommentDto>>? {
        val entity = localDataSource.getPage(type, cacheKey, self) ?: return null
        return try {
            val data = localDataSource.getCommentsForPage(type, cacheKey, self)
            val bookmark = CursorBookmark(
                prev = entity.prev,
                self = entity.self,
                next = entity.next
            )
            bookmark to CursorPageState.Success(bookmark, data)
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun loadAll(): List<Pair<CursorBookmark<String>, CursorPageState<String, CommentDto>>> =
        localDataSource.getAllPages(type, cacheKey).mapNotNull { load(it.self) }

    override suspend fun remove(self: String) = localDataSource.deletePage(type, cacheKey, self)

    override suspend fun removeAll(selves: List<String>) {
        selves.forEach { remove(it) }
    }

    override suspend fun clear() = localDataSource.deleteAllPages(type, cacheKey)

    override suspend fun <R> transaction(block: suspend CursorPersistentPagingCache<String, CommentDto>.() -> R): R {
        return block()
    }
}
