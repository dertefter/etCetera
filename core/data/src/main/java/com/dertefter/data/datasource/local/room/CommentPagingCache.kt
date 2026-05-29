package com.dertefter.data.datasource.local.room

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.entity.PageEntity
import com.dertefter.data.datasource.local.room.entity.PageType
import com.dertefter.data.dto.comments.CommentDto
import com.jamal_aliev.paginator.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cache.persistent.CursorPersistentPagingCache
import com.jamal_aliev.paginator.extension.isEmptyState
import com.jamal_aliev.paginator.page.PageState

class CommentPagingCache(
    private val cacheKey: String,
    private val localDataSource: LocalDataSource
) : CursorPersistentPagingCache<CommentDto> {

    private val type = PageType.COMMENT

    override suspend fun save(cursor: CursorBookmark, state: PageState<CommentDto>) {
        if (state is PageState.SuccessPage) {
            val self = cursor.self as String
            localDataSource.upsertPage(
                PageEntity(
                    type = type,
                    tab = cacheKey,
                    self = self,
                    prev = cursor.prev as? String,
                    next = cursor.next as? String,
                    isEmpty = state.isEmptyState()
                )
            )
            localDataSource.saveComments(type, cacheKey, self, state.data)
        }
    }

    override suspend fun saveAll(entries: List<Pair<CursorBookmark, PageState<CommentDto>>>) {
        entries.forEach { (cursor, state) -> save(cursor, state) }
    }

    override suspend fun load(self: Any): Pair<CursorBookmark, PageState<CommentDto>>? {
        val selfStr = self as String
        val entity = localDataSource.getPage(type, cacheKey, selfStr) ?: return null
        return try {
            val data = localDataSource.getCommentsForPage(type, cacheKey, selfStr)
            val bookmark = CursorBookmark(
                prev = entity.prev,
                self = entity.self,
                next = entity.next
            )
            bookmark to PageState.SuccessPage(0, data)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun loadAll(): List<Pair<CursorBookmark, PageState<CommentDto>>> =
        localDataSource.getAllPages(type, cacheKey).mapNotNull { load(it.self) }

    override suspend fun remove(self: Any) = localDataSource.deletePage(type, cacheKey, self as String)

    override suspend fun removeAll(selves: List<Any>) {
        selves.forEach { remove(it) }
    }

    override suspend fun clear() = localDataSource.deleteAllPages(type, cacheKey)

    override suspend fun <R> transaction(block: suspend CursorPersistentPagingCache<CommentDto>.() -> R): R {
        return block()
    }
}
