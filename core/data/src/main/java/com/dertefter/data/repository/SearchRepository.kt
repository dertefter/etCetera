package com.dertefter.data.repository

import com.dertefter.data.dto.search.SearchHashtagDto
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    fun getTrendingHashtags(): Flow<List<SearchHashtagDto>?>

    suspend fun updateTrendingHashtags(): Result<List<SearchHashtagDto>>

}
