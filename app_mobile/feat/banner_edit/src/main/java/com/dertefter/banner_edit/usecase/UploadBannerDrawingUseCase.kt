package com.dertefter.banner_edit.usecase

import android.content.Context
import android.graphics.Bitmap
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import com.dertefter.data.repository.AttachmentsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class UploadBannerDrawingUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val attachmentsRepository: AttachmentsRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): Result<AttachmentUploadResponseDto> {
        return try {
            val file = File(context.cacheDir, "banner_drawing.png")
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            attachmentsRepository.upload(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
