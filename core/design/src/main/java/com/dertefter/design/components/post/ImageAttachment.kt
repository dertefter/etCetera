package com.dertefter.design.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.dertefter.design.R
import com.dertefter.design.theme.AppTheme

@Composable
fun ImageAttachment(
    attachment: AttachmentUiModel,
    modifier: Modifier = Modifier
) {
    var retryHash by remember { mutableIntStateOf(0) }

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(attachment.url)
            .crossfade(true)
            .setParameter("retry_hash", retryHash)
            .build(),
        contentDescription = null,
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentScale = ContentScale.Crop,
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { retryHash++ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.design_retry),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

@Preview(showBackground = false)
@Composable
fun ImageAttachmentPreview() {
    AppTheme {
        ImageAttachment(
            attachment = AttachmentUiModel(
                id = "1",
                type = "image",
                url = "https://picsum.photos/400/300",
                mimeType = "image/jpeg"
            ),
            modifier = Modifier.size(200.dp)
        )
    }
}
