package com.dertefter.attachment_viewer

import androidx.lifecycle.ViewModel
import com.dertefter.attachment_viewer.presentation.Event
import com.dertefter.attachment_viewer.usecase.NavigateBackUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@ViewModelKey(AttachmentViewerViewModel::class)
@ContributesIntoMap(AppScope::class)
class AttachmentViewerViewModel @Inject constructor(
    private val navigateBackUseCase: NavigateBackUseCase
) : ViewModel() {

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnNavigateBack -> navigateBackUseCase()
        }
    }

}
