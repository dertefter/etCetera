package com.dertefter.crash_reports.presentation

import com.dertefter.data.dto.app.CrashlyticsItemDto

data class UiState(
    val reports: List<CrashlyticsItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val selectedReportContent: String? = null
)
