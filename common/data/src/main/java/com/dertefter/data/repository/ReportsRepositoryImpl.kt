package com.dertefter.data.repository

import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.reports.ReportDataDto
import com.dertefter.data.dto.reports.ReportRequestDto
import javax.inject.Inject

class ReportsRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val crashlyticsRepository: CrashlyticsRepository,
) : ReportsRepository {

    override suspend fun createReport(
        targetType: String,
        targetId: String,
        reason: String,
        description: String?
    ): Result<ReportDataDto> {
        val request = ReportRequestDto(
            targetType = targetType,
            targetId = targetId,
            reason = reason,
            description = description
        )
        return remoteDataSource.createReport(request).onFailureLog(crashlyticsRepository)
    }
}
