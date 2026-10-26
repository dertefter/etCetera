package com.dertefter.data.common

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.entity.PageType
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import com.dertefter.data.repository.CrashlyticsRepository
import kotlinx.coroutines.CancellationException

// Replacement for the deprecated L2 persistent cache (CursorPersistentPagingCache,
// deprecated since paginator 10.3.0): the database stays the single source of truth,
// each load persists its own page explicitly and falls back to it when the network fails.
// Inline so `remote`/`local` keep the caller's DSL receiver (`core`) and type inference.
suspend inline fun <T> loadCursorResultWithLocalFallback(
    localDataSource: LocalDataSource,
    crashlyticsRepository: CrashlyticsRepository,
    type: PageType,
    tab: String,
    cursor: CursorBookmark<String>?,
    remote: suspend () -> CursorLoadResult<String, T>,
    local: suspend () -> List<T>,
): CursorLoadResult<String, T> {
    val self = cursor?.self ?: "initial"
    return try {
        remote()
    } catch (remoteError: Throwable) {
        if (remoteError !is CancellationException) crashlyticsRepository.showError(remoteError)
        val entity = runCatching { localDataSource.getPage(type, tab, self) }.getOrNull()
        if (entity == null) throw remoteError
        android.util.Log.e("CursorLoad", "remote load failed, serving cached page $type/$tab/$self", remoteError)
        val bookmark = CursorBookmark(prev = entity.prev, self = entity.self, next = entity.next)
        CursorLoadResult(data = local(), bookmark = bookmark)
    }
}
