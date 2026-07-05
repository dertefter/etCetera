package com.dertefter.data.repository

import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import java.io.File

interface AttachmentsRepository {
    suspend fun upload(file: File): Result<AttachmentUploadResponseDto>
}
