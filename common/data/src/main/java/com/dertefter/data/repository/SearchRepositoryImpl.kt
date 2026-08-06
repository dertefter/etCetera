package com.dertefter.data.repository

import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.search.SearchDataDto
import com.dertefter.data.dto.search.SearchHashtagDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val crashlyticsRepository: CrashlyticsRepository,
) : SearchRepository {
    override fun getTrendingHashtags(): Flow<List<SearchHashtagDto>?> {
       return localDataSource.getTrendingHashtags()
    }

    override suspend fun updateTrendingHashtags(): Result<List<SearchHashtagDto>> {
        return remoteDataSource.getTrendingHashtags().onFailureLog(crashlyticsRepository).onSuccess {
            localDataSource.saveTrendingHashtags(it)
        }
    }

    override suspend fun getSearchResults(q: String): Result<SearchDataDto> {
        return remoteDataSource.getSearchResults(q).onFailureLog(crashlyticsRepository)
    }
}
