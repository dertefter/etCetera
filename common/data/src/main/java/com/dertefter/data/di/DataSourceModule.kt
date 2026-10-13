package com.dertefter.data.di

import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.LocalDataSourceImpl
import com.dertefter.data.datasource.local.TokenManager
import com.dertefter.data.datasource.local.TokenManagerImpl
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.datasource.remote.RemoteDataSourceImpl
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(AppScope::class)
interface DataSourceModule {

    @Binds
    fun bindRemoteDataSource(
        remoteDataSourceImpl: RemoteDataSourceImpl
    ): RemoteDataSource

    @Binds
    fun bindLocalDataSource(
        localDataSourceImpl: LocalDataSourceImpl
    ): LocalDataSource

    @Binds
    fun bindTokenManager(
        tokenManagerImpl: TokenManagerImpl
    ): TokenManager
}
