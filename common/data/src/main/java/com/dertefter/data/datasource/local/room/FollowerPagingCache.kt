package com.dertefter.data.datasource.local.room

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.entity.PageEntity
import com.dertefter.data.datasource.local.room.entity.PageType
import com.dertefter.data.dto.followers.FollowerUserDto
import com.jamal_aliev.paginator.core.extension.isEmptyState
import com.jamal_aliev.paginator.core.extension.isSuccessState
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.cache.persistent.CursorPersistentPagingCache
import com.jamal_aliev.paginator.cursor.page.CursorPageState

class FollowerPagingCache(
    private val tab: String,
    private val localDataSource: LocalDataSource
) : CursorPersistentPagingCache<String, FollowerUserDto> {

    private val type = PageType.USER

    override suspend fun save(cursor: CursorBookmark<String>, state: CursorPageState<String, FollowerUserDto>) {
        if (state.isSuccessState()) {
            val self = cursor.self
            localDataSource.upsertPage(
                PageEntity(
                    type = type,
                    tab = tab,
                    self = self,
                    prev = cursor.prev,
                    next = cursor.next,
                    isEmpty = state.isEmptyState()
                )
            )
            localDataSource.saveUsers(type, tab, self, state.data)
        }
    }

    override suspend fun saveAll(entries: List<Pair<CursorBookmark<String>, CursorPageState<String, FollowerUserDto>>>) {
        entries.forEach { (cursor, state) -> save(cursor, state) }
    }

    override suspend fun load(self: String): Pair<CursorBookmark<String>, CursorPageState<String, FollowerUserDto>>? {
        val entity = localDataSource.getPage(type, tab, self) ?: return null
        return try {
            val data = localDataSource.getUsersForPage(type, tab, self)
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

    override suspend fun loadAll(): List<Pair<CursorBookmark<String>, CursorPageState<String, FollowerUserDto>>> =
        localDataSource.getAllPages(type, tab).mapNotNull { load(it.self) }

    override suspend fun remove(self: String) = localDataSource.deletePage(type, tab, self)

    override suspend fun removeAll(selves: List<String>) {
        selves.forEach { remove(it) }
    }

    override suspend fun clear() = localDataSource.deleteAllPages(type, tab)

    override suspend fun <R> transaction(block: suspend CursorPersistentPagingCache<String, FollowerUserDto>.() -> R): R {
        return block()
    }
}
