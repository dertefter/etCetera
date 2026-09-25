package com.dertefter.user.usecase

import android.content.Context
import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.dto.me.UpdateMeResponseDto
import com.dertefter.data.repository.AttachmentsRepository
import com.dertefter.data.repository.MeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import javax.inject.Inject

class StoleBannerUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val meRepository: MeRepository,
    private val attachmentsRepository: AttachmentsRepository
) {
    suspend operator fun invoke(bannerUrl: String): Result<UpdateMeResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(bannerUrl)
                val file = File(context.cacheDir, "temp_banner_${System.currentTimeMillis()}.jpg")
                url.openStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                val uploadResult = attachmentsRepository.upload(file)
                if (uploadResult.isSuccess) {
                    val attachment = uploadResult.getOrThrow()
                    meRepository.saveMe(
                        UpdateMeRequestDto(
                            bannerId = attachment.id
                        )
                    )
                } else {
                    Result.failure(uploadResult.exceptionOrNull() ?: Exception("Failed to upload banner"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
