package com.dertefter.data.repository

import com.dertefter.data.common.loadCursorResultWithLocalFallback
import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.room.entity.PageEntity
import com.dertefter.data.datasource.local.room.entity.PageType
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.messenger.ChatDto
import com.dertefter.data.dto.messenger.MessageDto
import com.jamal_aliev.paginator.cursor.CursorPagingCore.Companion.UNLIMITED_CAPACITY
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.cache.eviction.CursorMostRecentPagingCache
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

@SingleIn(AppScope::class)
@Inject
class MessangerRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val crashlyticsRepository: CrashlyticsRepository,
) : MessangerRepository {

    private val activeChatsPaginators = CopyOnWriteArrayList<WeakReference<MutableCursorPaginator<String, ChatDto>>>()
    private val activeMessagesPaginators = CopyOnWriteArrayList<WeakReference<MutableCursorPaginator<String, MessageDto>>>()

    override fun getChatsPaginator(): MutableCursorPaginator<String, ChatDto> {
        val cacheKey = "all_chats"
        return mutableCursorPaginator(capacity = UNLIMITED_CAPACITY) {
            cache = CursorMostRecentPagingCache(maxSize = 50)
            load { cursor ->
                val requestCursor = if (cursor?.self == "initial") null else cursor?.self
                val result = loadCursorResultWithLocalFallback(
                    localDataSource = localDataSource,
                    crashlyticsRepository = crashlyticsRepository,
                    type = PageType.CHAT,
                    tab = cacheKey,
                    cursor = cursor,
                    remote = {
                        val data = remoteDataSource.getMessengerChats(limit = 100, before = requestCursor)
                            .onFailureLog(crashlyticsRepository).getOrThrow()

                        CursorLoadResult(
                            data = data.chats,
                            bookmark = CursorBookmark(
                                prev = cursor?.prev,
                                self = cursor?.self ?: "initial",
                                next = data.nextBefore
                            )
                        )
                    },
                    local = { emptyList() }
                )

                localDataSource.upsertPage(
                    PageEntity(
                        type = PageType.CHAT,
                        tab = cacheKey,
                        self = result.bookmark.self,
                        prev = result.bookmark.prev,
                        next = result.bookmark.next,
                        isEmpty = result.data.isEmpty()
                    )
                )

                result
            }
            initialCursor = CursorBookmark(prev = null, self = "initial", next = null)
        }.also {
            activeChatsPaginators.add(WeakReference(it))
        }
    }

    override fun getMessagesPaginatorForChat(chatId: String): MutableCursorPaginator<String, MessageDto> {
        val cacheKey = chatId
        return mutableCursorPaginator(capacity = UNLIMITED_CAPACITY) {
            cache = CursorMostRecentPagingCache(maxSize = 50)
            load { cursor ->
                val requestCursor = if (cursor?.self == "initial") null else cursor?.self
                val result = loadCursorResultWithLocalFallback(
                    localDataSource = localDataSource,
                    crashlyticsRepository = crashlyticsRepository,
                    type = PageType.MESSAGE,
                    tab = cacheKey,
                    cursor = cursor,
                    remote = {
                        val data = remoteDataSource.getMessengerMessages(chatId = chatId, limit = 50, before = requestCursor)
                            .onFailureLog(crashlyticsRepository).getOrThrow()

                        val fileIds = data.messages.flatMap { msg ->
                            msg.attachments.mapNotNull { it.fileId }
                        }.distinct()

                        val updatedMessages = if (fileIds.isNotEmpty()) {
                            val linksResult = remoteDataSource.getFileLinks(chatId, fileIds).getOrNull()
                            val linksMap = linksResult?.links?.associateBy { it.fileId } ?: emptyMap()

                            data.messages.map { msg ->
                                if (msg.attachments.isEmpty()) {
                                    msg
                                } else {
                                    val updatedAttachments = msg.attachments.map { att ->
                                        val linkInfo = att.fileId?.let { linksMap[it] }
                                        if (linkInfo != null) {
                                            att.copy(
                                                url = linkInfo.url ?: att.url,
                                                thumbUrl = linkInfo.thumbUrl ?: att.thumbUrl,
                                                expiresIn = linkInfo.expiresIn ?: att.expiresIn
                                            )
                                        } else {
                                            att
                                        }
                                    }
                                    msg.copy(attachments = updatedAttachments)
                                }
                            }
                        } else {
                            data.messages
                        }

                        val nextCursor = if (updatedMessages.isNotEmpty()) updatedMessages.lastOrNull()?.messageId else null

                        CursorLoadResult(
                            data = updatedMessages,
                            bookmark = CursorBookmark(
                                prev = cursor?.prev,
                                self = cursor?.self ?: "initial",
                                next = nextCursor
                            )
                        )
                    },
                    local = { emptyList() }
                )

                localDataSource.upsertPage(
                    PageEntity(
                        type = PageType.MESSAGE,
                        tab = cacheKey,
                        self = result.bookmark.self,
                        prev = result.bookmark.prev,
                        next = result.bookmark.next,
                        isEmpty = result.data.isEmpty()
                    )
                )

                result
            }
            initialCursor = CursorBookmark(prev = null, self = "initial", next = null)
        }.also {
            activeMessagesPaginators.add(WeakReference(it))
        }
    }
}
