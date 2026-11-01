package com.dertefter.banner_edit.engine

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

private const val SS = 10

class Coverage(val w: Int, val h: Int) {
    val cov: FloatArray = FloatArray(w * h)
    val touched: IntArray = IntArray(w * h)
    var n = 0
        private set

    fun clear() {
        for (k in 0 until n) cov[touched[k]] = 0f
        n = 0
    }

    private fun put(i: Int, c: Float) {
        if (c <= cov[i]) return
        if (cov[i] == 0f) touched[n++] = i
        cov[i] = c
    }

    fun capsule(ax: Double, ay: Double, bx: Double, by: Double, r: Double) {
        val e = r + 1
        val x0 = max(0, floor(min(ax, bx) - e).toInt())
        val x1 = min(w - 1, ceil(max(ax, bx) + e).toInt())
        val y0 = max(0, floor(min(ay, by) - e).toInt())
        val y1 = min(h - 1, ceil(max(ay, by) + e).toInt())
        if (x0 > x1 || y0 > y1) return
        val dx = bx - ax
        val dy = by - ay
        val l2 = dx * dx + dy * dy
        val inv = if (l2 != 0.0) 1 / l2 else 0.0
        val lim = (r + 0.5) * (r + 0.5)
        val core = if (r > 0.5) (r - 0.5) * (r - 0.5) else -1.0
        for (y in y0..y1) {
            val py = y + 0.5
            for (x in x0..x1) {
                val px = x + 0.5
                var t = ((px - ax) * dx + (py - ay) * dy) * inv
                t = if (t < 0) 0.0 else if (t > 1) 1.0 else t
                val ex = ax + t * dx - px
                val ey = ay + t * dy - py
                val d2 = ex * ex + ey * ey
                if (d2 >= lim) continue
                put(y * w + x, if (d2 <= core) 1f else min(1f, (r + 0.5 - sqrt(d2)).toFloat()))
            }
        }
    }

    fun chain(p: List<Pt>, r: Double) {
        val cnt = p.size
        val segs = max(1, cnt - 1)
        if (r >= 1.5) {
            for (k in 0 until segs) {
                val a = p[k]
                val b = p[min(k + 1, cnt - 1)]
                capsule(a.x, a.y, b.x, b.y, r)
            }
            return
        }
        val e = r + 1
        val hi = (r + 0.7072) * (r + 0.7072)
        val lo = if (r > 0.7072) (r - 0.7072) * (r - 0.7072) else -1.0
        val r2 = r * r
        val ax = DoubleArray(segs)
        val ay = DoubleArray(segs)
        val dx = DoubleArray(segs)
        val dy = DoubleArray(segs)
        val inv = DoubleArray(segs)
        for (k in 0 until segs) {
            val a = p[k]
            val b = p[min(k + 1, cnt - 1)]
            ax[k] = a.x; ay[k] = a.y
            dx[k] = b.x - a.x; dy[k] = b.y - a.y
            val l2 = dx[k] * dx[k] + dy[k] * dy[k]
            inv[k] = if (l2 != 0.0) 1 / l2 else 0.0
        }

        fun d2(qx: Double, qy: Double, k: Int): Double {
            var t = ((qx - ax[k]) * dx[k] + (qy - ay[k]) * dy[k]) * inv[k]
            t = if (t < 0) 0.0 else if (t > 1) 1.0 else t
            val ex = ax[k] + t * dx[k] - qx
            val ey = ay[k] + t * dy[k] - qy
            return ex * ex + ey * ey
        }

        fun nearV(x: Int, y: Int, vx: Double, vy: Double): Boolean {
            val ex = max(abs(vx - x - 0.5) - 0.5, 0.0)
            val ey = max(abs(vy - y - 0.5) - 0.5, 0.0)
            return ex * ex + ey * ey < r2
        }

        for (k in 0 until segs) {
            val bx = ax[k] + dx[k]
            val by = ay[k] + dy[k]
            val x0 = max(0, floor(min(ax[k], bx) - e).toInt())
            val x1 = min(w - 1, ceil(max(ax[k], bx) + e).toInt())
            val y0 = max(0, floor(min(ay[k], by) - e).toInt())
            val y1 = min(h - 1, ceil(max(ay[k], by) + e).toInt())
            for (y in y0..y1) for (x in x0..x1) {
                val cx = x + 0.5
                val cy = y + 0.5
                val c2 = d2(cx, cy, k)
                if (c2 >= hi) continue
                if (c2 <= lo) { put(y * w + x, 1f); continue }
                val nearA = nearV(x, y, ax[k], ay[k])
                val nearB = nearV(x, y, bx, by)
                if (nearA && !nearB && k > 0) continue
                if (!nearA && !nearB) { put(y * w + x, min(1f, (r + 0.5 - sqrt(c2)).toFloat())); continue }
                val kp = if (k > 0) k - 1 else -1
                val kn = if (k + 1 < segs) k + 1 else -1
                var inside = 0
                for (sy in 0 until SS) {
                    val qy = y + (sy + 0.5) / SS
                    for (sx in 0 until SS) {
                        val qx = x + (sx + 0.5) / SS
                        if (d2(qx, qy, k) <= r2 || (kp >= 0 && d2(qx, qy, kp) <= r2) || (kn >= 0 && d2(qx, qy, kn) <= r2)) inside++
                    }
                }
                if (inside > 0) put(y * w + x, inside.toFloat() / (SS * SS))
            }
        }
    }

    fun ring(cx: Double, cy: Double, r0: Double, r: Double) {
        val e = r0 + r + 1
        val x0 = max(0, floor(cx - e).toInt())
        val x1 = min(w - 1, ceil(cx + e).toInt())
        val y0 = max(0, floor(cy - e).toInt())
        val y1 = min(h - 1, ceil(cy + e).toInt())
        for (y in y0..y1) for (x in x0..x1) {
            val d = abs(hypot(x + 0.5 - cx, y + 0.5 - cy) - r0)
            if (d < r + 0.5) put(y * w + x, min(1f, (r + 0.5 - d).toFloat()))
        }
    }

    fun op(o: Op) {
        val r = o.width / 2
        val p = o.points
        when (o.tool) {
            TOOL_CIRCLE -> {
                val a = p[0]
                val b = p[p.size - 1]
                ring(a.x, a.y, hypot(b.x - a.x, b.y - a.y), r)
            }
            TOOL_RECTANGLE -> {
                val (x0, y0) = p[0]
                val (x1, y1) = p[p.size - 1]
                capsule(x0, y0, x1, y0, r); capsule(x1, y0, x1, y1, r)
                capsule(x1, y1, x0, y1, r); capsule(x0, y1, x0, y0, r)
            }
            else -> chain(p, r)
        }
    }
}

fun opRgb(o: Op): IntArray = if (o.tool == TOOL_ERASER) intArrayOf(255, 255, 255) else fromHex(o.color)

fun composite(rgb: FloatArray, cv: Coverage, c: IntArray) {
    for (k in 0 until cv.n) {
        val i = cv.touched[k]
        val a = cv.cov[i].toDouble()
        val j = i * 3
        rgb[j] += ((c[0] - rgb[j]) * a).toFloat()
        rgb[j + 1] += ((c[1] - rgb[j + 1]) * a).toFloat()
        rgb[j + 2] += ((c[2] - rgb[j + 2]) * a).toFloat()
    }
}

fun renderOps(ops: List<Op>, w: Int, h: Int, cv: Coverage = Coverage(w, h)): FloatArray {
    val rgb = FloatArray(w * h * 3) { 255f }
    for (o in ops) {
        cv.op(o)
        composite(rgb, cv, opRgb(o))
        cv.clear()
    }
    return rgb
}
