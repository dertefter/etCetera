package com.dertefter.data.repository

import com.dertefter.data.common.onFailureLog
import com.dertefter.data.datasource.remote.RemoteDataSource
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttachmentsRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val crashlyticsRepository: CrashlyticsRepository,
) : AttachmentsRepository {
    override suspend fun upload(file: File): Result<AttachmentUploadResponseDto> {
        return remoteDataSource.uploadMyFile(file).onFailureLog(crashlyticsRepository)
    }
}
