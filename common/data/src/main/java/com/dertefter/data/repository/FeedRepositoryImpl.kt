package com.dertefter.data.repository

import android.util.Log
import com.dertefter.data.common.loadCursorResultWithLocalFallback
import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.entity.PageEntity
import com.dertefter.data.datasource.local.room.entity.PageType
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.feed.PostDto
import com.jamal_aliev.paginator.core.logger.LogLevel
import com.jamal_aliev.paginator.core.logger.PrintPaginatorLogger
import com.jamal_aliev.paginator.cursor.CursorPagingCore.Companion.UNLIMITED_CAPACITY
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.cache.eviction.CursorMostRecentPagingCache
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@Inject
class FeedRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val postRepository: PostRepository,
    private val crashlyticsRepository: CrashlyticsRepository,
) : FeedRepository {

    override fun getFeedPaginator(tab: String): MutableCursorPaginator<String, PostDto> {
        return mutableCursorPaginator(capacity = UNLIMITED_CAPACITY) {
            cache = CursorMostRecentPagingCache(maxSize = 20)
            initialCursor = CursorBookmark(prev = null, self = "initial", next = null)
            logger = PrintPaginatorLogger(minLevel = LogLevel.DEBUG)
            load { cursor ->

                val result = loadCursorResultWithLocalFallback<PostDto>(
                    localDataSource = localDataSource,
                    crashlyticsRepository = crashlyticsRepository,
                    type = PageType.POST,
                    tab = tab,
                    cursor = cursor,
                    remote = {
                        val data = remoteDataSource.getPosts(
                            tab,
                            cursor?.self?.takeIf { it != "initial" }
                        ).onFailureLog(crashlyticsRepository).getOrThrow()
                        Log.e("paginator", "loaded for $cursor || next is ${data.pagination.nextCursor}")
                        CursorLoadResult(
                            data = data.posts,
                            bookmark = CursorBookmark(
                                prev = cursor?.prev,
                                self = cursor?.self ?: "initial",
                                next = if (data.pagination.hasMore) data.pagination.nextCursor else null
                            )
                        )
                    },
                    local = { localDataSource.getPostsForPage(PageType.POST, tab, cursor?.self ?: "initial") }
                )

                localDataSource.upsertPage(
                    PageEntity(
                        type = PageType.POST,
                        tab = tab,
                        self = result.bookmark.self,
                        prev = result.bookmark.prev,
                        next = result.bookmark.next,
                        isEmpty = result.data.isEmpty()
                    )
                )
                localDataSource.savePosts(PageType.POST, tab, result.bookmark.self, result.data)

                result
            }

        }.also {
            postRepository.registerPaginator(it)
        }
    }

    override fun getHashtagPaginator(hashtag: String): MutableCursorPaginator<String, PostDto> {
        return mutableCursorPaginator(capacity = 20) {
            cache = CursorMostRecentPagingCache(maxSize = 20)

            load { cursor ->
                val result = loadCursorResultWithLocalFallback<PostDto>(
                    localDataSource = localDataSource,
                    crashlyticsRepository = crashlyticsRepository,
                    type = PageType.POST,
                    tab = hashtag,
                    cursor = cursor,
                    remote = {
                        val data = remoteDataSource.getPostsForHashtag(hashtag, cursor?.self?.takeIf { it != "initial" })
                            .onFailureLog(crashlyticsRepository).getOrThrow()
                        CursorLoadResult(
                            data = data.posts,
                            bookmark = CursorBookmark(
                                prev = cursor?.prev,
                                self = cursor?.self ?: "initial",
                                next = if (data.pagination.hasMore) data.pagination.nextCursor else null
                            )
                        )
                    },
                    local = { localDataSource.getPostsForPage(PageType.POST, hashtag, cursor?.self ?: "initial") }
                )

                localDataSource.upsertPage(
                    PageEntity(
                        type = PageType.POST,
                        tab = hashtag,
                        self = result.bookmark.self,
                        prev = result.bookmark.prev,
                        next = result.bookmark.next,
                        isEmpty = result.data.isEmpty()
                    )
                )
                localDataSource.savePosts(PageType.POST, hashtag, result.bookmark.self, result.data)

                result
            }
            initialCursor = CursorBookmark(prev = null, self = "initial", next = null)
        }.also {
            postRepository.registerPaginator(it)
        }
    }

    override fun getPostsPaginator(userId: String, sort: String, pinnedPostId: () -> String?): MutableCursorPaginator<String, PostDto> {
        val cacheKey = "user_$userId" + "_$sort"
        return mutableCursorPaginator(capacity = 20) {
            cache = CursorMostRecentPagingCache(maxSize = 20)

            load { cursor ->
                val result = loadCursorResultWithLocalFallback<PostDto>(
                    localDataSource = localDataSource,
                    crashlyticsRepository = crashlyticsRepository,
                    type = PageType.POST,
                    tab = cacheKey,
                    cursor = cursor,
                    remote = {
                        val data = remoteDataSource.getPosts(
                            userId, sort = sort, pinnedPostId = pinnedPostId(),
                            cursor = cursor?.self?.takeIf { it != "initial" }
                        ).onFailureLog(crashlyticsRepository).getOrThrow()
                        CursorLoadResult(
                            data = data.posts,
                            bookmark = CursorBookmark(
                                prev = cursor?.prev,
                                self = cursor?.self ?: "initial",
                                next = if (data.pagination.hasMore) data.pagination.nextCursor else null
                            )
                        )
                    },
                    local = { localDataSource.getPostsForPage(PageType.POST, cacheKey, cursor?.self ?: "initial") }
                )

                localDataSource.upsertPage(
                    PageEntity(
                        type = PageType.POST,
                        tab = cacheKey,
                        self = result.bookmark.self,
                        prev = result.bookmark.prev,
                        next = result.bookmark.next,
                        isEmpty = result.data.isEmpty()
                    )
                )
                localDataSource.savePosts(PageType.POST, cacheKey, result.bookmark.self, result.data)

                result
            }
            initialCursor = CursorBookmark(prev = null, self = "initial", next = null)
        }.also {
            postRepository.registerPaginator(it)
        }
    }

    override fun getLikedPostsPaginator(userId: String): MutableCursorPaginator<String, PostDto> {
        val cacheKey = "liked_user_$userId"
        return mutableCursorPaginator(capacity = 20) {
            cache = CursorMostRecentPagingCache(maxSize = 20)

            load { cursor ->
                val result = loadCursorResultWithLocalFallback<PostDto>(
                    localDataSource = localDataSource,
                    crashlyticsRepository = crashlyticsRepository,
                    type = PageType.POST,
                    tab = cacheKey,
                    cursor = cursor,
                    remote = {
                        val data = remoteDataSource.getLikedPosts(userId, cursor?.self?.takeIf { it != "initial" })
                            .onFailureLog(crashlyticsRepository).getOrThrow()
                        CursorLoadResult(
                            data = data.posts,
                            bookmark = CursorBookmark(
                                prev = cursor?.prev,
                                self = cursor?.self ?: "initial",
                                next = if (data.pagination.hasMore) data.pagination.nextCursor else null
                            )
                        )
                    },
                    local = { localDataSource.getPostsForPage(PageType.POST, cacheKey, cursor?.self ?: "initial") }
                )

                localDataSource.upsertPage(
                    PageEntity(
                        type = PageType.POST,
                        tab = cacheKey,
                        self = result.bookmark.self,
                        prev = result.bookmark.prev,
                        next = result.bookmark.next,
                        isEmpty = result.data.isEmpty()
                    )
                )
                localDataSource.savePosts(PageType.POST, cacheKey, result.bookmark.self, result.data)

                result
            }
            initialCursor = CursorBookmark(prev = null, self = "initial", next = null)
        }.also {
            postRepository.registerPaginator(it)
        }
    }
}
