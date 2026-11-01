package com.dertefter.data.dto.banner

import kotlinx.serialization.Serializable


@Serializable
data class BannerOpDto(
    val tool: String,
    val color: String,
    val width: Double,
    val points: List<List<Double>>
)

@Serializable
data class BannerDrawingDto(
    val version: Int,
    val width: Int,
    val height: Int,
    val ops: List<BannerOpDto>
)
