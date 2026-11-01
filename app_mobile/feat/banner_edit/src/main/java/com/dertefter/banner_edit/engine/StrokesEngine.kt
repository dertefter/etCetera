package com.dertefter.banner_edit.engine


import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class StrokesOptions(
    val strokes: Int,
    val onProgress: ((done: Double) -> Unit)? = null,
)

private const val SCALE = 2
private const val CANDIDATES = 32
private const val CLIMB = 256
private const val MAX_POINTS = 8
private const val MAX_LEN = 8
private const val RESAMPLE = 25

private class Stroke(val p: DoubleArray, val r: Double)

private class Gain(val gain: Double, val l: Double, val a: Double, val b: Double)

fun strokes(src: Rgba, opt: StrokesOptions): List<Op> {
    val img = downsample(src, SCALE)
    val w = img.width
    val h = img.height
    val n = w * h
    val t = labOf(img.data, n)
    val cur = FloatArray(n * 3)
    val err = FloatArray(n)
    val white = oklab(255, 255, 255)
    for (i in 0 until n) {
        cur[i * 3] = white[0].toFloat()
        cur[i * 3 + 1] = white[1].toFloat()
        cur[i * 3 + 2] = white[2].toFloat()
        err[i] = dist(cur, t, i).toFloat()
    }
    val idx = IntArray(n)
    val cov = FloatArray(n)
    val cum = DoubleArray(n)
    val mark = IntArray(n)
    val most = FloatArray(n)
    val rMax = LIMITS.MAX_WIDTH / 2 / SCALE
    var seed = 0x9e3779b9L
    var stamp = 0

    fun rnd(): Double {
        seed = (seed * 1664525L + 1013904223L) and 0xFFFFFFFFL
        return seed / 4294967296.0
    }

    fun cover(s: Stroke): Int {
        val p = s.p
        val m = p.size / 2
        val r = s.r
        val e = r + 1
        val lim = (r + 0.5) * (r + 0.5)
        val ns = max(1, m - 1)
        var k = 0
        stamp++
        for (j in 0 until ns) {
            val a = min(j, m - 1) * 2
            val b = min(j + 1, m - 1) * 2
            val ax = p[a]
            val ay = p[a + 1]
            val dx = p[b] - ax
            val dy = p[b + 1] - ay
            val l2 = dx * dx + dy * dy
            val inv = if (l2 != 0.0) 1 / l2 else 0.0
            val x0 = max(0, floor(min(ax, p[b]) - e).toInt())
            val x1 = min(w - 1, ceilToInt(max(ax, p[b]) + e))
            val y0 = max(0, floor(min(ay, p[b + 1]) - e).toInt())
            val y1 = min(h - 1, ceilToInt(max(ay, p[b + 1]) + e))
            for (y in y0..y1) for (x in x0..x1) {
                val px = x + 0.5
                val py = y + 0.5
                var tt = ((px - ax) * dx + (py - ay) * dy) * inv
                tt = if (tt < 0) 0.0 else if (tt > 1) 1.0 else tt
                val ex = ax + tt * dx - px
                val ey = ay + tt * dy - py
                val d2 = ex * ex + ey * ey
                if (d2 >= lim) continue
                val c = min(1.0, r + 0.5 - sqrt(d2))
                val i = y * w + x
                if (mark[i] != stamp) {
                    mark[i] = stamp
                    most[i] = c.toFloat()
                    idx[k++] = i
                } else if (c > most[i]) {
                    most[i] = c.toFloat()
                }
            }
        }
        for (q in 0 until k) cov[q] = most[idx[q]]
        return k
    }

    fun score(s: Stroke): Gain {
        val k = cover(s)
        var sa = 0.0
        var sl = 0.0
        var sA = 0.0
        var sB = 0.0
        for (q in 0 until k) {
            val j = idx[q] * 3
            val a = cov[q].toDouble()
            val b = 1 - a
            sa += a * a
            sl += a * (t[j] - b * cur[j])
            sA += a * (t[j + 1] - b * cur[j + 1])
            sB += a * (t[j + 2] - b * cur[j + 2])
        }
        if (sa == 0.0) return Gain(0.0, 0.0, 0.0, 0.0)
        val l = sl / sa
        val a = sA / sa
        val b = sB / sa
        var gain = 0.0
        for (q in 0 until k) {
            val i = idx[q]
            val j = i * 3
            val aa = cov[q].toDouble()
            val bb = 1 - aa
            val dl = bb * cur[j] + aa * l - t[j]
            val da = bb * cur[j + 1] + aa * a - t[j + 1]
            val db = bb * cur[j + 2] + aa * b - t[j + 2]
            gain += err[i] - sqrt((dl * dl + da * da + db * db))
        }
        return Gain(gain, l, a, b)
    }

    fun fits(s: Stroke): Boolean {
        val lim = (MAX_LEN * max(1.0, s.r)) * (MAX_LEN * max(1.0, s.r))
        var j = 2
        while (j < s.p.size) {
            val dx = s.p[j] - s.p[j - 2]
            val dy = s.p[j + 1] - s.p[j - 1]
            if (dx * dx + dy * dy > lim) return false
            j += 2
        }
        return true
    }

    fun mutate(base: Stroke): Stroke {
        var p = base.p.copyOf()
        val m = p.size / 2
        val step = max(0.5, base.r) * (rnd() * 2 - 1)
        val u = rnd()
        if (u < 0.15) return Stroke(p, min(rMax, max(0.5, base.r * (1 + (rnd() - 0.5) * 0.6))))
        if (u < 0.3 && m < MAX_POINTS) {
            val j = floor(rnd() * (m + 1)).toInt()
            if (j == 0 || j == m) {
                // продлеваем с конца в сторону последнего звена с поворотом
                val ei = if (j != 0) m - 1 else 0
                val fi = if (j != 0) max(0, m - 2) else min(1, m - 1)
                var dx = p[ei * 2] - p[fi * 2]
                var dy = p[ei * 2 + 1] - p[fi * 2 + 1]
                var l = hypot(dx, dy)
                if (l == 0.0) {
                    val th0 = rnd() * PI
                    dx = cos(th0)
                    dy = sin(th0)
                    l = 1.0
                }
                val len = max(1.0, base.r) * (1 + rnd() * 3)
                val th = (rnd() - 0.5) * 1.5
                val co = cos(th)
                val si = sin(th)
                val nx = p[ei * 2] + (dx * co - dy * si) / l * len
                val ny = p[ei * 2 + 1] + (dx * si + dy * co) / l * len
                p = if (j != 0) p + doubleArrayOf(nx, ny) else doubleArrayOf(nx, ny) + p
            } else {
                // изгиб: середина звена со сдвигом поперёк
                val ax = p[(j - 1) * 2]
                val ay = p[(j - 1) * 2 + 1]
                val bx = p[j * 2]
                val by = p[j * 2 + 1]
                val l = hypot(bx - ax, by - ay).let { if (it == 0.0) 1.0 else it }
                val np = DoubleArray(p.size + 2)
                val cut = j * 2
                for (i in 0 until cut) np[i] = p[i]
                np[cut] = (ax + bx) / 2 - (by - ay) / l * step
                np[cut + 1] = (ay + by) / 2 + (bx - ax) / l * step
                for (i in cut until p.size) np[i + 2] = p[i]
                p = np
            }
            return Stroke(p, base.r)
        }
        if (u < 0.4 && m > 2) {
            val at = floor(rnd() * m).toInt() * 2
            val np = DoubleArray(p.size - 2)
            for (i in 0 until at) np[i] = p[i]
            for (i in at + 2 until p.size) np[i - 2] = p[i]
            return Stroke(np, base.r)
        }
        p[floor(rnd() * p.size).toInt()] += step
        return Stroke(p, base.r)
    }

    fun resampleErr() {
        var s = 0.0
        for (i in 0 until n) {
            val v = err[i].toDouble()
            s += v * v
            cum[i] = s
        }
    }

    // точка, где начать мазок: чем больше ошибка, тем вероятнее
    fun pick(): Int {
        val v = rnd() * cum[n - 1]
        var lo = 0
        var hi = n - 1
        while (lo < hi) {
            val m = (lo + hi) shr 1
            if (cum[m] < v) lo = m + 1 else hi = m
        }
        return lo
    }

    val ops = mutableListOf<Op>()
    for (s0 in 0 until opt.strokes) {
        if (s0 % RESAMPLE == 0) {
            resampleErr()
            opt.onProgress?.invoke(s0.toDouble() / opt.strokes)
        }
        if (cum[n - 1] == 0.0) break
        var found: Stroke? = null
        var bs = Gain(0.0, 0.0, 0.0, 0.0)
        repeat(CANDIDATES) {
            val q = pick()
            val x = q % w + 0.5
            val y = (q - q % w).toDouble() / w + 0.5
            val u0 = rnd()
            val r = max(0.5, rMax * u0 * u0)
            val th = rnd() * PI
            val half = rnd() * r * 2
            val cand = Stroke(
                doubleArrayOf(
                    x - cos(th) * half, y - sin(th) * half,
                    x + cos(th) * half, y + sin(th) * half,
                ),
                r,
            )
            val sc = score(cand)
            if (sc.gain > bs.gain) { bs = sc; found = cand }
        }
        val first = found ?: continue
        var best = first
        repeat(CLIMB) {
            val mu = mutate(best)
            if (!fits(mu)) return@repeat
            val sc = score(mu)
            if (sc.gain > bs.gain) { bs = sc; best = mu }
        }

        val rgb = fromOklab(bs.l, bs.a, bs.b)
        val color = toHex(rgb[0], rgb[1], rgb[2])
        val hx = fromHex(color)
        val labC = oklab(hx[0], hx[1], hx[2])
        val k = cover(best)
        for (q in 0 until k) {
            val i = idx[q]
            val j = i * 3
            val a = cov[q].toDouble()
            for (ch in 0 until 3) cur[j + ch] = (cur[j + ch] + (labC[ch] - cur[j + ch]) * a).toFloat()
            err[i] = dist(cur, t, i).toFloat()
        }
        val points = mutableListOf<Pt>()
        var jj = 0
        while (jj < best.p.size) {
            val pt = Pt(
                roundCompat(best.p[jj] * SCALE * 100) / 100.0,
                roundCompat(best.p[jj + 1] * SCALE * 100) / 100.0,
            )
            val l = points.lastOrNull()
            if (l == null || l.x != pt.x || l.y != pt.y) points.add(pt)
            jj += 2
        }
        ops.add(
            Op(
                tool = TOOL_BRUSH,
                color = color,
                width = min(LIMITS.MAX_WIDTH, roundCompat(best.r * 2 * SCALE * 100) / 100.0),
                points = points,
            )
        )
    }
    opt.onProgress?.invoke(1.0)
    return ops
}

private fun dist(c: FloatArray, t: FloatArray, i: Int): Double {
    val j = i * 3
    val dl = c[j] - t[j]
    val da = c[j + 1] - t[j + 1]
    val db = c[j + 2] - t[j + 2]
    return sqrt((dl * dl + da * da + db * db).toDouble())
}
private fun roundCompat(v: Double): Double = floor(v + 0.5)

private fun ceilToInt(v: Double): Int = ceilCompat(v).toInt()

private fun ceilCompat(v: Double): Double = -floor(-v)
