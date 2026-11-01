package com.dertefter.banner_edit.engine


const val TOOL_BRUSH = "brush"
const val TOOL_LINE = "line"
const val TOOL_RECTANGLE = "rectangle"
const val TOOL_CIRCLE = "circle"
const val TOOL_ERASER = "eraser"

data class Pt(val x: Double, val y: Double)

data class Op(
    val tool: String,
    var color: String,
    var width: Double,
    val points: List<Pt>,
)

class Rgba(
    val width: Int,
    val height: Int,
    val data: IntArray,
)

class Stats(
    val ops: Int,
    val points: Int,
    val bytes: Int,
    val maxPointsPerOp: Int,
)

typealias OnProgress = (stage: String, done: Double) -> Unit

internal fun putClamped(arr: IntArray, i: Int, v: Double) {
    arr[i] = if (v.isNaN()) 0 else if (v <= 0.0) 0 else if (v >= 255.0) 255 else v.roundToIntCompat()
}

internal fun Double.roundToIntCompat(): Int = kotlin.math.floor(this + 0.5).toInt()
