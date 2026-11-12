package com.dertefter.data.repository

import com.dertefter.data.dto.ebdi.EbdiUser
import com.jamal_aliev.paginator.offset.MutablePaginator

interface EbdiRepository {

    fun getUsersPaginator(clan: String): MutablePaginator<EbdiUser>
}
