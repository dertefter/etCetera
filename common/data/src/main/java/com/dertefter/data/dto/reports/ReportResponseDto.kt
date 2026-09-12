package com.dertefter.data.dto.reports

import kotlinx.serialization.Serializable

@Serializable
data class ReportResponseDto(
    val data: ReportDataDto
)

@Serializable
data class ReportDataDto(
    val id: String,
    val createdAt: String
)
