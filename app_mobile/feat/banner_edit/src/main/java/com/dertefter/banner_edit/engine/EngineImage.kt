package com.dertefter.banner_edit.engine


import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


data class Adjust(
    val brightness: Double,
    val contrast: Double,
    val saturation: Double,
    val denoise: Double,
)

val NO_ADJUST = Adjust(brightness = 0.0, contrast = 0.0, saturation = 0.0, denoise = 0.0)

fun adjust(src: Rgba, a: Adjust): Rgba {
    val width = src.width
    val height = src.height
    val n = width * height
    if (a.brightness == 0.0 && a.contrast == 0.0 && a.saturation == 0.0 && a.denoise == 0.0) return src
    var data = src.data.copyOf()
    if (a.brightness != 0.0 || a.contrast != 0.0 || a.saturation != 0.0) {
        val c = if (a.contrast >= 0) 1 + a.contrast * 2 else 1 + a.contrast
        val b = a.brightness * 128
        val s = 1 + a.saturation
        for (i in 0 until n) {
            val j = i * 4
            var r = data[j].toDouble()
            var g = data[j + 1].toDouble()
            var bl = data[j + 2].toDouble()
            val y = 0.299 * r + 0.587 * g + 0.114 * bl
            r = y + (r - y) * s
            g = y + (g - y) * s
            bl = y + (bl - y) * s
            putClamped(data, j, (r - 128) * c + 128 + b)
            putClamped(data, j + 1, (g - 128) * c + 128 + b)
            putClamped(data, j + 2, (bl - 128) * c + 128 + b)
        }
    }
    if (a.denoise > 0) data = denoise(data, width, height, a.denoise)
    return Rgba(width, height, data)
}

private fun denoise(data: IntArray, w: Int, h: Int, d: Double): IntArray {
    val r = (d * 2 * 1000).roundToIntCompat() / 1000.0
    val r0 = floor(r).toInt()
    val f = r - r0
    val eps = 0.0004 * d * d * 65025
    val lo = if (r0 > 0) guided(data, w, h, r0, eps) else data
    return if (f == 0.0) lo else mix(lo, guided(data, w, h, r0 + 1, eps), f)
}

private fun mix(a: IntArray, b: IntArray, f: Double): IntArray {
    val out = IntArray(a.size)
    for (i in a.indices) putClamped(out, i, a[i] + (b[i] - a[i]) * f)
    return out
}

private fun boxMean(src: FloatArray, w: Int, h: Int, r: Int): FloatArray {
    val tmp = FloatArray(w * h)
    val out = FloatArray(w * h)
    for (y in 0 until h) {
        var s = 0.0
        val o = y * w
        for (x in -r..r) s += src[o + min(w - 1, max(0, x))].toDouble()
        for (x in 0 until w) {
            tmp[o + x] = (s / (2 * r + 1)).toFloat()
            s += src[o + min(w - 1, x + r + 1)].toDouble() - src[o + max(0, x - r)].toDouble()
        }
    }
    for (x in 0 until w) {
        var s = 0.0
        for (y in -r..r) s += tmp[min(h - 1, max(0, y)) * w + x].toDouble()
        for (y in 0 until h) {
            out[y * w + x] = (s / (2 * r + 1)).toFloat()
            s += tmp[min(h - 1, y + r + 1) * w + x].toDouble() - tmp[max(0, y - r) * w + x].toDouble()
        }
    }
    return out
}

private fun guided(data: IntArray, w: Int, h: Int, r: Int, eps: Double): IntArray {
    val n = w * h
    val out = data.copyOf()
    val i1 = FloatArray(n)
    val i2 = FloatArray(n)
    for (ch in 0 until 3) {
        for (i in 0 until n) {
            val v = data[i * 4 + ch].toDouble()
            i1[i] = v.toFloat()
            i2[i] = (v * v).toFloat()
        }
        val m = boxMean(i1, w, h, r)
        val m2 = boxMean(i2, w, h, r)
        val a = FloatArray(n)
        val b = FloatArray(n)
        for (i in 0 until n) {
            val v = (m2[i] - m[i] * m[i]).toDouble()
            val av = v / (v + eps)
            a[i] = av.toFloat()
            b[i] = (m[i] - av * m[i]).toFloat()
        }
        val ma = boxMean(a, w, h, r)
        val mb = boxMean(b, w, h, r)
        for (i in 0 until n) putClamped(out, i * 4 + ch, (ma[i] * i1[i] + mb[i]).toDouble())
    }
    return out
}

fun downsample(src: Rgba, cell: Int): Rgba {
    if (cell == 1) return src
    val w = src.width
    val h = src.height
    val data = src.data
    val cols = ceilDiv(w, cell)
    val rows = ceilDiv(h, cell)
    val out = IntArray(cols * rows * 4)
    for (gy in 0 until rows) for (gx in 0 until cols) {
        var r = 0.0
        var g = 0.0
        var b = 0.0
        var c = 0
        val y1 = min(h, gy * cell + cell)
        val x1 = min(w, gx * cell + cell)
        for (y in gy * cell until y1) for (x in gx * cell until x1) {
            val j = (y * w + x) * 4
            r += data[j]
            g += data[j + 1]
            b += data[j + 2]
            c++
        }
        val k = (gy * cols + gx) * 4
        out[k] = (r / c).roundToInt()
        out[k + 1] = (g / c).roundToInt()
        out[k + 2] = (b / c).roundToInt()
        out[k + 3] = 255
    }
    return Rgba(cols, rows, out)
}

private fun ceilDiv(a: Int, b: Int): Int = (a + b - 1) / b
