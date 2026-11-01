package com.dertefter.banner_edit.presentation.drawbox

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


data class BannerStroke(
    val colorHex: String,
    val width: Float,
    val points: List<Offset>
)

fun Color.toHexRrggbb(): String = "#" + "%06x".format(this.toArgb() and 0xFFFFFF)
