package com.dertefter.user.presentation.mapper

import android.content.Context
import com.dertefter.data.dto.user.LastSeenDto
import com.dertefter.user.R

fun LastSeenDto?.toPresentationString(context: Context, online: Boolean): String? {
    if (online) return context.getString(R.string.user_online)
    if (this == null) return null

    val formatted = when (this) {
        is LastSeenDto.JustNow -> context.getString(R.string.user_last_seen_just_now)
        is LastSeenDto.Minutes -> {
            val n = value.takeIf { it > 0 } ?: 1
            context.resources.getQuantityString(R.plurals.user_last_seen_minutes, n, n)
        }
        is LastSeenDto.Hours -> {
            val n = value.takeIf { it > 0 } ?: 1
            context.resources.getQuantityString(R.plurals.user_last_seen_hours, n, n)
        }
        is LastSeenDto.Recently -> context.getString(R.string.user_last_seen_recently)
        is LastSeenDto.ThisWeek -> context.getString(R.string.user_last_seen_this_week)
        is LastSeenDto.ThisMonth -> context.getString(R.string.user_last_seen_this_month)
        is LastSeenDto.LongAgo -> context.getString(R.string.user_last_seen_long_ago)
    }

    return "${context.getString(R.string.user_last_seen_prefix)}$formatted"
}
