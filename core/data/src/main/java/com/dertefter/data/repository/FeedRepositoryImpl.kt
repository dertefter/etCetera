package com.dertefter.data.repository

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.PostPagingCache
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.feed.PostDto
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.cache.eviction.CursorMostRecentPagingCache
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val postRepository: PostRepository
) : FeedRepository {

    override fun getFeedPaginator(tab: String): MutableCursorPaginator<String, PostDto> {
        return mutableCursorPaginator(capacity = 20) {
            cache = CursorMostRecentPagingCache(maxSize = 20)
            persistentCache = PostPagingCache(tab, localDataSource)
            initialCursor = CursorBookmark(prev = null, self = "initial", next = null)


            load { cursor ->
                val result = remoteDataSource.getPosts(
                    tab,
                    cursor?.self?.takeIf { it != "initial" }
                )

                val data = result.getOrThrow()

                CursorLoadResult(
                    data = data.posts,
                    bookmark = CursorBookmark(
                        prev = cursor?.prev,
                        self = cursor?.self ?: "initial",
                        next = if (data.pagination.hasMore) data.pagination.nextCursor else null
                    )
                )
            }

        }.also {
            postRepository.registerPaginator(it)
        }
    }

    override fun getHashtagPaginator(hashtag: String): MutableCursorPaginator<String, PostDto> {
        return mutableCursorPaginator(capacity = 20) {
            cache = CursorMostRecentPagingCache(maxSize = 20)
            persistentCache = PostPagingCache(hashtag, localDataSource)

            load { cursor ->
                val result = remoteDataSource.getPostsForHashtag(hashtag, cursor?.self?.takeIf { it != "initial" } )
                val data = result.getOrThrow()

                CursorLoadResult(
                    data = data.posts,
                    bookmark = CursorBookmark(
                        prev = cursor?.prev,
                        self = cursor?.self ?: "initial",
                        next = if (data.pagination.hasMore) data.pagination.nextCursor else null
                    )
                )
            }
            initialCursor = CursorBookmark(prev = null, self = "initial", next = null)
        }.also {
            postRepository.registerPaginator(it)
        }
    }

    override fun getPostsPaginator(userId: String, sort: String, pinnedPostId: String?): MutableCursorPaginator<String, PostDto> {
        val cacheKey = "user_$userId" + "_$sort"
        return mutableCursorPaginator(capacity = 20) {
            cache = CursorMostRecentPagingCache(maxSize = 20)
            persistentCache = PostPagingCache(cacheKey, localDataSource)

            load { cursor ->
                val result = remoteDataSource.getPosts(
                    userId, sort = sort, pinnedPostId = pinnedPostId,
                    cursor = cursor?.self?.takeIf { it != "initial" }
                )
                val data = result.getOrThrow()

                CursorLoadResult(
                    data = data.posts,
                    bookmark = CursorBookmark(
                        prev = cursor?.prev,
                        self = cursor?.self ?: "initial",
                        next = if (data.pagination.hasMore) data.pagination.nextCursor else null
                    )
                )
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
            persistentCache = PostPagingCache(cacheKey, localDataSource)

            load { cursor ->
                val result = remoteDataSource.getLikedPosts(userId, cursor?.self?.takeIf { it != "initial" })
                val data = result.getOrThrow()

                CursorLoadResult(
                    data = data.posts,
                    bookmark = CursorBookmark(
                        prev = cursor?.prev,
                        self = cursor?.self ?: "initial",
                        next = if (data.pagination.hasMore) data.pagination.nextCursor else null
                    )
                )
            }
            initialCursor = CursorBookmark(prev = null, self = "initial", next = null)
        }.also {
            postRepository.registerPaginator(it)
        }
    }
}
