package com.dertefter.data.datasource.remote

import com.dertefter.data.dto.ebdi.EbdiUser
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface EbdiApiService {

    @GET("users")
    suspend fun users(
        @Query("order") order: String = "followers_count",
        @Query("descending") descending: Boolean = true,
        @Query("offset") offset: Int = 0,
        @Query("clan") clan: String,
        @Query("exists") exists: Boolean = true
    ): Response<List<EbdiUser>>

}