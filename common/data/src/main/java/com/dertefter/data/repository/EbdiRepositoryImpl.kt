package com.dertefter.data.repository

import com.dertefter.data.datasource.remote.EbdiApiService
import com.dertefter.data.dto.ebdi.EbdiUser
import com.jamal_aliev.paginator.offset.MutablePaginator
import com.jamal_aliev.paginator.offset.dsl.mutablePaginator
import com.jamal_aliev.paginator.offset.load.LoadResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@Inject
class EbdiRepositoryImpl(
    private val ebdiApiService: EbdiApiService
) : EbdiRepository {

    override fun getUsersPaginator(clan: String): MutablePaginator<EbdiUser> {
        return mutablePaginator {
            load { page ->
                val offset = (page - 1) * core.capacity
                val response = ebdiApiService.users(
                    offset = offset,
                    clan = clan
                )
                if (response.isSuccessful) {
                    val users = response.body().orEmpty()
                    LoadResult(users)
                } else {
                    val errorBody = response.errorBody()?.string()
                    throw Exception("API Error ${response.code()}: $errorBody")
                }
            }
        }
    }
}
