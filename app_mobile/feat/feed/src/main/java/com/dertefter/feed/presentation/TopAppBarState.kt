package com.dertefter.feed.presentation

import com.dertefter.data.dto.search.SearchHashtagDto

data class TopBarUiState(
    val trendingHashtags: List<SearchHashtagDto>? = null,
    val notificationsCount: Int? = null,
    val avatarEmoji: String? = null
)
