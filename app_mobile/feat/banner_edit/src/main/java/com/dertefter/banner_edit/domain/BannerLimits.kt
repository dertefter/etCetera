package com.dertefter.banner_edit.domain

import com.dertefter.banner_edit.presentation.drawbox.BannerStroke
import com.dertefter.data.dto.banner.BannerDrawingDto
import com.dertefter.data.dto.banner.BannerOpDto
import kotlin.math.min
import kotlin.math.roundToInt

object BannerLimits {
    const val W = 1100
    const val H = 380

    const val MAX_OPS = 2000
    const val MAX_POINTS = 60_000
    const val MAX_POINTS_PER_OP = 4096
    const val MAX_WIDTH = 100.0

    private val HEX = Regex("^#[0-9a-fA-F]{6}$")

    fun buildDrawing(strokes: List<BannerStroke>): Result<BannerDrawingDto> {
        val ops = strokes
            .filter { it.points.isNotEmpty() }
            .flatMap { splitLong(it) }
            .map { it.toOpDto() }
        val errors = validate(ops)
        if (errors.isNotEmpty()) return Result.failure(IllegalStateException(errors.joinToString("; ")))
        return Result.success(BannerDrawingDto(version = 1, width = W, height = H, ops = ops))
    }

    private fun splitLong(stroke: BannerStroke): List<BannerStroke> {
        val n = MAX_POINTS_PER_OP
        if (stroke.points.size <= n) return listOf(stroke)
        val out = mutableListOf<BannerStroke>()
        var i = 0
        while (i < stroke.points.size - 1) {
            val end = min(i + n, stroke.points.size)
            out.add(stroke.copy(points = stroke.points.subList(i, end).toList()))
            i += n - 1
        }
        return out
    }

    private fun BannerStroke.toOpDto(): BannerOpDto = BannerOpDto(
        tool = "brush",
        color = colorHex,
        width = width.coerceIn(0.1f, MAX_WIDTH.toFloat()).toDouble(),
        points = points.map { listOf(it.x.roundToInt().toDouble(), it.y.roundToInt().toDouble()) }
    )


    private fun validate(ops: List<BannerOpDto>): List<String> {
        val errs = mutableListOf<String>()
        if (ops.isEmpty()) errs.add("Рисунок пустой")
        if (ops.size > MAX_OPS) errs.add("Штрихов ${ops.size}, можно $MAX_OPS")
        val points = ops.sumOf { it.points.size }
        if (points > MAX_POINTS) errs.add("Точек $points, можно $MAX_POINTS")
        for (o in ops) {
            if (o.points.size > MAX_POINTS_PER_OP) { errs.add("В штрихе ${o.points.size} точек, можно $MAX_POINTS_PER_OP"); break }
            if (!HEX.matches(o.color)) { errs.add("Цвет ${o.color} не в формате #rrggbb"); break }
            if (!(o.width > 0.0 && o.width <= MAX_WIDTH)) { errs.add("Ширина ${o.width} вне 0…100"); break }
        }
        return errs
    }
}
