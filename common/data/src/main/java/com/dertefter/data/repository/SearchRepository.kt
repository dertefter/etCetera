package com.dertefter.data.repository

import com.dertefter.data.dto.search.SearchDataDto
import com.dertefter.data.dto.search.SearchHashtagDto
import com.dertefter.data.dto.search.TopClanDto
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    fun getTrendingHashtags(): Flow<List<SearchHashtagDto>?>

    suspend fun updateTrendingHashtags(): Result<List<SearchHashtagDto>>

    fun getTopClans(): Flow<List<TopClanDto>?>

    suspend fun updateTopClans(): Result<List<TopClanDto>>

    suspend fun getSearchResults(q: String): Result<SearchDataDto>

}
