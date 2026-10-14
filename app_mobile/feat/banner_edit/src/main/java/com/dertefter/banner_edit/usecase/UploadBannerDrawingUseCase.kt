package com.dertefter.banner_edit.usecase

import android.content.Context
import android.graphics.Bitmap
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import com.dertefter.data.repository.AttachmentsRepository
import java.io.File
import dev.zacsweers.metro.Inject

@Inject
class UploadBannerDrawingUseCase(
    private val context: Context,
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
