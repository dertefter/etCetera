package com.dertefter.attachment_viewer.presentation

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.dertefter.attachment_viewer.R
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.post.Attachment
import com.dertefter.design.components.post.AttachmentUiModel
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.spacing
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState


@Composable
fun AttachmentViewerScreen(
    onEvent: (Event) -> Unit,
    attachments: List<AttachmentUiModel>,
    viewPosition: Int = 0,
) {
    val pagerState = rememberPagerState(
        initialPage = viewPosition,
        pageCount = { attachments.size }
    )
    val context = LocalContext.current

    val hazeState = rememberHazeState()

    Scaffold(
        containerColor = Color.Black
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .hazeSource(state = hazeState)
                    .fillMaxSize(),
                pageSpacing = 16.dp,
                userScrollEnabled = true,
            ) { page ->
                val attachment = attachments[page]
                Attachment(
                    isFullscreen = true,
                    attachment = attachment,
                    containerColor = Color.Black,
                    contentPadding = contentPadding
                )

            }

            AppNavigationIcon(
                onClick = {
                    onEvent(Event.OnNavigateBack)
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(contentPadding)
                    .padding(MaterialTheme.spacing.defaultScreenPadding),
                containerColor = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White,
                hazeState = hazeState
            )

            if (attachments.size > 1) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${attachments.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(contentPadding)
                        .padding(MaterialTheme.spacing.defaultScreenPadding)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            AppNavigationIcon(
                onClick = {
                    downloadAttachment(context, attachments[pagerState.currentPage])
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(contentPadding)
                    .padding(MaterialTheme.spacing.defaultScreenPadding),
                containerColor = Color.Black.copy(alpha = 0.5f),
                icon = Icons.Download,
                contentDescription = stringResource(R.string.attachment_viewer_download),
                contentColor = Color.White,
                hazeState = hazeState
            )
        }
    }
}

private fun downloadAttachment(context: Context, attachment: AttachmentUiModel) {
    try {
        val url = attachment.url ?: return
        val mimeType = attachment.mimeType ?: when (attachment.type) {
            "video" -> "video/mp4"
            "image" -> "image/jpeg"
            else -> "application/octet-stream"
        }
        val extension = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "bin"

        val request = DownloadManager.Request(url.toUri())
            .setMimeType(mimeType)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "etCetera_${System.currentTimeMillis()}.$extension"
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    } catch (e: Exception) {
        val text = context.getString(R.string.attachment_viewer_download_failed)
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}
