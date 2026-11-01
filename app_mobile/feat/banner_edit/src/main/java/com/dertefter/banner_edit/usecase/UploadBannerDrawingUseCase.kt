package com.dertefter.banner_edit.usecase

import com.dertefter.banner_edit.domain.BannerLimits
import com.dertefter.banner_edit.presentation.drawbox.BannerStroke
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import com.dertefter.data.repository.AttachmentsRepository
import dev.zacsweers.metro.Inject

@Inject
class UploadBannerDrawingUseCase(
    private val attachmentsRepository: AttachmentsRepository
) {
    suspend operator fun invoke(strokes: List<BannerStroke>): Result<AttachmentUploadResponseDto> {
        return BannerLimits.buildDrawing(strokes)
            .mapCatching { drawing -> attachmentsRepository.uploadBannerDrawing(drawing).getOrThrow() }
    }
}
