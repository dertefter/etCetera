package com.dertefter.data.repository

import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import java.io.File
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@Inject
class AttachmentsRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val crashlyticsRepository: CrashlyticsRepository,
) : AttachmentsRepository {
    override suspend fun upload(file: File): Result<AttachmentUploadResponseDto> {
        return remoteDataSource.uploadMyFile(file).onFailureLog(crashlyticsRepository)
    }
}
