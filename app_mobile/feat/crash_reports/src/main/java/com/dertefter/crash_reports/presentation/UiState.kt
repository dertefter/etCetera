package com.dertefter.crash_reports.presentation

import com.dertefter.data.dto.app.CrashlyticsItem

data class UiState(
    val reports: List<CrashlyticsItem> = emptyList(),
    val isLoading: Boolean = false,
    val selectedReportContent: String? = null
)
