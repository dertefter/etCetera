package com.dertefter.attachment_viewer

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dertefter.design.components.post.AttachmentUiModel
import com.dertefter.attachment_viewer.presentation.AttachmentViewerScreen
import com.dertefter.navigation.AttachmentNavigationModel

@Composable
fun AttachmentViewerRoute(
    attachments: List<AttachmentNavigationModel>,
    viewPosition: Int = 0,
    viewModel: AttachmentViewerViewModel = hiltViewModel(),
) {

    AttachmentViewerScreen(
        onEvent = { event ->
            viewModel.onEvent(event)
        },
        viewPosition = viewPosition,
        attachments = attachments.map { it.toUiModel() }
    )
}

fun AttachmentNavigationModel.toUiModel() = AttachmentUiModel(id, type, url, mimeType)
