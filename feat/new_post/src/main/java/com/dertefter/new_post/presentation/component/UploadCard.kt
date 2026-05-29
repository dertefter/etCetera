package com.dertefter.new_post.presentation.component

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.components.post.Attachment
import com.dertefter.design.components.post.AttachmentUiModel
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.new_post.presentation.Upload
import com.dertefter.new_post.presentation.UploadStatus

@Composable
fun UploadCard(
    modifier: Modifier = Modifier,
    upload: Upload,
    onRetry: () -> Unit = {},
    onDelete: () -> Unit = {}
){

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .size(240.dp, 180.dp),
        contentAlignment = Alignment.Center
    ){

        if (upload.attachment == null) {
            AsyncImage(
                model = upload.uri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        upload.attachment?.let{ attachment ->
            Attachment(
                modifier = Modifier.fillMaxSize(),
                attachment = AttachmentUiModel(
                    id = attachment.id,
                    type = "",
                    url = attachment.url,
                    mimeType = attachment.mimeType
                )
            )
        }

        if (upload.uploadStatus == UploadStatus.UPLOADING || upload.uploadStatus == UploadStatus.ERROR) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f))
            )
        }

        when (upload.uploadStatus){
            UploadStatus.UPLOADING -> {
                AppLoadingIndicator(
                    color = MaterialTheme.colorScheme.primaryFixed
                )
            }

            UploadStatus.ERROR -> {
                FilledTonalIconButton(
                    onClick = onRetry
                ) {
                    Icon(
                        imageVector = Icons.Refresh,
                        contentDescription = null,
                    )
                }
            }
            else -> {}
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(MaterialTheme.spacing.small)
                .size(36.dp),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Icon(
                contentDescription = null,
                imageVector = Icons.Close
            )
        }


    }

}

@Preview
@Composable
fun UploadCardPreview() {
    AppTheme {
        UploadCard(
            upload = Upload(
                uploadStatus = UploadStatus.SUCCESS,
                uri = Uri.EMPTY,
                attachment = AttachmentUploadResponseDto(
                    id = "1",
                    url = "https://picsum.photos/400/300",
                    mimeType = "image/jpeg"
                )
            )
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UploadCardUploadingPreview() {
    AppTheme {
        UploadCard(
            upload = Upload(
                uploadStatus = UploadStatus.UPLOADING,
                uri = Uri.EMPTY,
                attachment = null
            )
        )
    }
}

@Preview
@Composable
fun UploadCardErrorPreview() {
    AppTheme {
        UploadCard(
            upload = Upload(
                uploadStatus = UploadStatus.ERROR,
                uri = Uri.EMPTY,
                attachment = null
            )
        )
    }
}
