package com.dertefter.attachment_viewer

import androidx.compose.runtime.Composable
import com.dertefter.design.components.post.AttachmentUiModel
import com.dertefter.attachment_viewer.presentation.AttachmentViewerScreen
import com.dertefter.navigation.AttachmentNavigationModel
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun AttachmentViewerRoute(
    attachments: List<AttachmentNavigationModel>,
    viewPosition: Int = 0,
    viewModel: AttachmentViewerViewModel = metroViewModel(),
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
