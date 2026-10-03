package com.dertefter.attachment_viewer

import androidx.compose.runtime.Composable
import com.dertefter.attachment_viewer.presentation.AttachmentViewerScreen
import com.dertefter.design.components.post.AttachmentUiModel
import com.dertefter.navigation.AttachmentNavigationModel

@Composable
fun AttachmentViewerRoute(
    attachments: List<AttachmentNavigationModel>,
    viewPosition: Int = 0,
) {

    AttachmentViewerScreen(
        viewPosition = viewPosition,
        attachments = attachments.map { it.toUiModel() }
    )
}

fun AttachmentNavigationModel.toUiModel() = AttachmentUiModel(id, type, url, mimeType)
