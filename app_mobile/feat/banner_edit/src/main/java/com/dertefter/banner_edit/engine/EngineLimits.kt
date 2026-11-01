package com.dertefter.banner_edit.engine

import kotlin.math.max

object LIMITS {
    const val W = 1100
    const val H = 380

    const val OPS = 2000
    const val POINTS = 60000
    const val POINTS_PER_OP = 4096
    const val MAX_WIDTH = 100.0
    const val BODY_BYTES = 2 shl 20
}

private val HEX = Regex("^#[0-9a-fA-F]{6}$")


fun drawing(ops: List<Op>) = BannerDrawing(version = 1, width = LIMITS.W, height = LIMITS.H, ops = ops)


data class BannerDrawing(val version: Int, val width: Int, val height: Int, val ops: List<Op>)

fun stats(ops: List<Op>): Stats {
    var points = 0
    var maxPointsPerOp = 0
    for (o in ops) {
        points += o.points.size
        maxPointsPerOp = max(maxPointsPerOp, o.points.size)
    }
    return Stats(ops = ops.size, points = points, bytes = approxJsonBytes(ops), maxPointsPerOp = maxPointsPerOp)
}


private fun approxJsonBytes(ops: List<Op>): Int = buildString {
    append("""{"version":1,"width":1100,"height":380,"ops":[""")
    for (o in ops) {
        append("""{"tool":"","color":"","width":""")
        append(o.width)
        append(""","points":[""")
        for (p in o.points) {
            append('[').append(p.x).append(',').append(p.y).append("],")
        }
        append("]}}")
    }
    append("]}")
}.toByteArray(Charsets.UTF_8).size

fun validate(ops: List<Op>): List<String> {
    val s = stats(ops)
    val errs = mutableListOf<String>()
    if (ops.isEmpty()) errs.add("Рисунок пустой")
    if (s.ops > LIMITS.OPS) errs.add("Штрихов ${s.ops}, можно ${LIMITS.OPS}")
    if (s.points > LIMITS.POINTS) errs.add("Точек ${s.points}, можно ${LIMITS.POINTS}")
    if (s.maxPointsPerOp > LIMITS.POINTS_PER_OP) errs.add("В штрихе ${s.maxPointsPerOp} точек, можно ${LIMITS.POINTS_PER_OP}")
    if (s.bytes > LIMITS.BODY_BYTES) errs.add("Запрос ${"%.1f".format(s.bytes / 1048576.0)} МБ, можно 2 МБ")
    for (o in ops) {
        if (!HEX.matches(o.color)) { errs.add("Цвет ${o.color} не в формате #rrggbb"); break }
        if (!(o.width > 0 && o.width <= LIMITS.MAX_WIDTH)) { errs.add("Ширина ${o.width} вне 0…100"); break }
        if (o.points.isEmpty() || o.points.any { !it.x.isFinite() || !it.y.isFinite() }) { errs.add("Штрих без точек или с неверной координатой"); break }
    }
    return errs
}


fun splitLong(ops: List<Op>): List<Op> {
    val out = mutableListOf<Op>()
    val n = LIMITS.POINTS_PER_OP
    for (o in ops) {
        if (o.points.size <= n || o.tool != TOOL_BRUSH) { out.add(o); continue }
        var i = 0
        while (i < o.points.size - 1) {
            out.add(o.copy(points = o.points.subList(i, minOf(i + n, o.points.size)).toList()))
            i += n - 1
        }
    }
    return out
}
