package com.dertefter.banner_edit.engine


import kotlin.math.roundToInt
import kotlin.math.sqrt

class Quality(
    val dE: Double,
    val bad: Double,
    val map: IntArray,
)

fun compare(rgb: FloatArray, target: FloatArray, n: Int): Quality {
    val map = IntArray(n)
    val cache = HashMap<Int, DoubleArray>()
    var sum = 0.0
    var bad = 0
    for (i in 0 until n) {
        val r = rgb[i * 3].roundToInt()
        val g = rgb[i * 3 + 1].roundToInt()
        val b = rgb[i * 3 + 2].roundToInt()
        val k = (r shl 16) or (g shl 8) or b
        var p = cache[k]
        if (p == null) {
            p = oklab(r, g, b)
            cache[k] = p
        }
        val dl = p[0] - target[i * 3]
        val da = p[1] - target[i * 3 + 1]
        val db = p[2] - target[i * 3 + 2]
        val d = sqrt(dl * dl + da * da + db * db) * 100
        sum += d
        if (d > 5) bad++
        map[i] = if (d > 255) 255 else d.toInt()
    }
    return Quality(dE = sum / n, bad = bad.toDouble() / n, map = map)
}
