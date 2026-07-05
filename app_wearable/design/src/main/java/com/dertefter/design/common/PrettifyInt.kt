package com.dertefter.design.common

import java.util.Locale

fun Int.PrettifyInt(): String {
    if (this in -999..999) return this.toString()
    val value = this.toDouble()
    val suffix: String
    val divisor: Double
    if (this >= 1_000_000 || this <= -1_000_000) {
        suffix = "M"
        divisor = 1_000_000.0
    } else {
        suffix = "k"
        divisor = 1_000.0
    }
    val formatted = String.format(Locale.US, "%.1f", value / divisor)
    return if (formatted.endsWith(".0")) {
        formatted.substring(0, formatted.length - 2) + suffix
    } else {
        formatted + suffix
    }
}
