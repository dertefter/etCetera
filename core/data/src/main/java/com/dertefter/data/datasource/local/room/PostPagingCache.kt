package com.dertefter.data.datasource.local.room

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.entity.PageEntity
import com.dertefter.data.datasource.local.room.entity.PageType
import com.dertefter.data.dto.feed.PostDto
import com.jamal_aliev.paginator.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cache.persistent.CursorPersistentPagingCache
import com.jamal_aliev.paginator.extension.isEmptyState
import com.jamal_aliev.paginator.page.PageState

class PostPagingCache(
    private val tab: String,
    private val localDataSource: LocalDataSource
) : CursorPersistentPagingCache<PostDto> {

    private val type = PageType.POST

    override suspend fun save(cursor: CursorBookmark, state: PageState<PostDto>) {
        if (state is PageState.SuccessPage) {
            val self = cursor.self as String
            localDataSource.upsertPage(
                PageEntity(
                    type = type,
                    tab = tab,
                    self = self,
                    prev = cursor.prev as? String,
                    next = cursor.next as? String,
                    isEmpty = state.isEmptyState()
                )
            )
            localDataSource.savePosts(type, tab, self, state.data)
        }
    }

    override suspend fun saveAll(entries: List<Pair<CursorBookmark, PageState<PostDto>>>) {
        entries.forEach { (cursor, state) -> save(cursor, state) }
    }

    override suspend fun load(self: Any): Pair<CursorBookmark, PageState<PostDto>>? {
        val selfStr = self as String
        val entity = localDataSource.getPage(type, tab, selfStr) ?: return null
        return try {
            val data = localDataSource.getPostsForPage(type, tab, selfStr)
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

    override suspend fun loadAll(): List<Pair<CursorBookmark, PageState<PostDto>>> =
        localDataSource.getAllPages(type, tab).mapNotNull { load(it.self) }

    override suspend fun remove(self: Any) = localDataSource.deletePage(type, tab, self as String)

    override suspend fun removeAll(selves: List<Any>) {
        selves.forEach { remove(it) }
    }

    override suspend fun clear() = localDataSource.deleteAllPages(type, tab)

    override suspend fun <R> transaction(block: suspend CursorPersistentPagingCache<PostDto>.() -> R): R {
        return block()
    }
}
