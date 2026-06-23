package com.dertefter.new_comment.presentation

import android.net.Uri
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto

data class Upload(
    val uploadStatus: UploadStatus,
    val uri: Uri,
    val attachment: AttachmentUploadResponseDto?,
)
