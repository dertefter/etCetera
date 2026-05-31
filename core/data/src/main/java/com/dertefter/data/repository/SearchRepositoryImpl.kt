package com.dertefter.data.repository

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.search.SearchHashtagDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) : SearchRepository {
    override fun getTrendingHashtags(): Flow<List<SearchHashtagDto>?> {
       return localDataSource.getTrendingHashtags()
    }

    override suspend fun updateTrendingHashtags(): Result<List<SearchHashtagDto>> {
        return remoteDataSource.getTrendingHashtags().onSuccess {
            localDataSource.saveTrendingHashtags(it)
        }
    }
}
