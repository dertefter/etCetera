package com.dertefter.banner_edit.engine

import kotlin.math.cbrt
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

private val LIN = FloatArray(256) {
    val v = it / 255.0
    if (v <= 0.04045) (v / 12.92).toFloat() else ((v + 0.055) / 1.055).pow(2.4).toFloat()
}

fun oklabLin(lr: Double, lg: Double, lb: Double): DoubleArray {
    val l = cbrt(0.4122214708 * lr + 0.5363325363 * lg + 0.0514459929 * lb)
    val m = cbrt(0.2119034982 * lr + 0.6806995451 * lg + 0.1073969566 * lb)
    val s = cbrt(0.0883024619 * lr + 0.2817188376 * lg + 0.6299787005 * lb)
    return doubleArrayOf(
        0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s,
        1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s,
        0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s,
    )
}


fun oklab(r: Int, g: Int, b: Int): DoubleArray = oklabLin(LIN[r].toDouble(), LIN[g].toDouble(), LIN[b].toDouble())

fun oklabF(r: Double, g: Double, b: Double): DoubleArray {
    fun f(v0: Double): Double {
        val v = min(255.0, max(0.0, v0)) / 255.0
        return if (v <= 0.04045) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
    }
    return oklabLin(f(r), f(g), f(b))
}

fun labOf(data: IntArray, n: Int): FloatArray {
    val out = FloatArray(n * 3)
    val cache = HashMap<Int, DoubleArray>()
    for (i in 0 until n) {
        val r = data[i * 4]
        val g = data[i * 4 + 1]
        val b = data[i * 4 + 2]
        val k = (r shl 16) or (g shl 8) or b
        var p = cache[k]
        if (p == null) {
            p = oklab(r, g, b)
            if (cache.size < (1 shl 18)) cache[k] = p
        }
        out[i * 3] = p[0].toFloat()
        out[i * 3 + 1] = p[1].toFloat()
        out[i * 3 + 2] = p[2].toFloat()
    }
    return out
}

private fun labToRgb(L: Double, a: Double, b: Double): DoubleArray {
    val l = (L + 0.3963377774 * a + 0.2158037573 * b).pow(3)
    val m = (L - 0.1055613458 * a - 0.0638541728 * b).pow(3)
    val s = (L - 0.0894841775 * a - 1.2914855480 * b).pow(3)

    fun enc(v: Double): Double = 255.0 * (if (v <= 0.0031308) 12.92 * v else 1.055 * max(0.0, v).pow(1.0 / 2.4) - 0.055)

    return doubleArrayOf(
        enc(4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s),
        enc(-1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s),
        enc(-0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s),
    )
}

fun fromOklab(L: Double, a: Double, b: Double): DoubleArray {
    fun ok(c: DoubleArray): Boolean =
        c[0] >= -0.5 && c[0] <= 255.5 && c[1] >= -0.5 && c[1] <= 255.5 && c[2] >= -0.5 && c[2] <= 255.5

    var rgb = labToRgb(L, a, b)
    if (!ok(rgb)) {
        var lo = 0.0
        var hi = 1.0
        for (i in 0 until 16) {
            val k = (lo + hi) / 2
            if (ok(labToRgb(L, a * k, b * k))) lo = k else hi = k
        }
        rgb = labToRgb(L, a * lo, b * lo)
    }
    return DoubleArray(3) { min(255.0, max(0.0, rgb[it])) }
}

private fun hex2(n: Double): String {
    val v = min(255.0, max(0.0, n)).roundToInt()
    return "%02x".format(v)
}

fun toHex(r: Double, g: Double, b: Double): String = "#" + hex2(r) + hex2(g) + hex2(b)

fun toHex(r: Int, g: Int, b: Int): String = "#" + "%02x".format(min(255, max(0, r))) + "%02x".format(min(255, max(0, g))) + "%02x".format(min(255, max(0, b)))

fun fromHex(h: String): IntArray = intArrayOf(
    h.substring(1, 3).toInt(16),
    h.substring(3, 5).toInt(16),
    h.substring(5, 7).toInt(16),
)
