package com.dertefter.data.repository

import com.dertefter.data.dto.messenger.ChatDto
import com.dertefter.data.dto.messenger.MessageDto
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator

interface MessangerRepository {

    fun getChatsPaginator(): MutableCursorPaginator<String, ChatDto>

    fun getMessagesPaginatorForChat(chatId: String): MutableCursorPaginator<String, MessageDto>

}