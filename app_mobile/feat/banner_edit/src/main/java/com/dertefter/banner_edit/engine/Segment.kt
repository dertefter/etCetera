package com.dertefter.banner_edit.engine

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

private val N8X = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
private val N8Y = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)


fun despeckle(cols: Int, rows: Int, label: IntArray, k: Int): IntArray {
    val out = label.copyOf()
    val cnt = IntArray(k)
    for (y in 0 until rows) for (x in 0 until cols) {
        val i = y * cols + x
        val own = label[i]
        val y0 = max(0, y - 1)
        val y1 = min(rows - 1, y + 1)
        val x0 = max(0, x - 1)
        val x1 = min(cols - 1, x + 1)
        var best = own
        var bestN = 0
        for (yy in y0..y1) for (xx in x0..x1) {
            val l = label[yy * cols + xx]
            val c = ++cnt[l]
            if (l != own && c > bestN) { bestN = c; best = l }
        }
        if (cnt[own] <= 2 && bestN >= 5) out[i] = best
        for (yy in y0..y1) for (xx in x0..x1) cnt[label[yy * cols + xx]] = 0
    }
    return out
}

fun smoothLabels(cols: Int, rows: Int, label: IntArray, k: Int, minVotes: Int = 0): IntArray {
    val out = label.copyOf()
    val cnt = IntArray(k)
    for (y in 0 until rows) for (x in 0 until cols) {
        val i = y * cols + x
        val y0 = max(0, y - 1)
        val y1 = min(rows - 1, y + 1)
        val x0 = max(0, x - 1)
        val x1 = min(cols - 1, x + 1)
        var best = label[i]
        for (yy in y0..y1) for (xx in x0..x1) {
            val l = label[yy * cols + xx]
            if (++cnt[l] > cnt[best]) best = l
        }
        if (cnt[best] >= minVotes) out[i] = best
        for (yy in y0..y1) for (xx in x0..x1) cnt[label[yy * cols + xx]] = 0
    }
    return out
}

private val PX = intArrayOf(-1, 1, 0, 0, -1, 1, -1, 1)
private val PY = intArrayOf(0, 0, -1, 1, -1, -1, 1, 1)
private val PW = doubleArrayOf(1.0, 1.0, 1.0, 1.0, 0.7071067811865476, 0.7071067811865476, 0.7071067811865476, 0.7071067811865476)

private const val EDGE_T = 0.12
private const val EDGE_PERP = 0.25
private const val EDGE_THIN = 0.85

private const val EDGE_SIDES = 0.3

fun absorbEdges(cols: Int, rows: Int, label: IntArray, k: Int, lab: FloatArray) {
    val total = cols * rows
    val edge = ByteArray(total)
    val take = IntArray(total) { -1 }

    fun nb(i: Int, kk: Int): Int {
        val x = i % cols
        return when (kk) {
            0 -> if (x > 0) i - 1 else -1
            1 -> if (x < cols - 1) i + 1 else -1
            2 -> if (i >= cols) i - cols else -1
            else -> if (i + cols < total) i + cols else -1
        }
    }

    for (i in 0 until total) for (kk in 0 until 4) {
        val n = nb(i, kk)
        if (n >= 0 && label[n] != label[i]) { edge[i] = 1; break }
    }

    fun between(c: Int, a: Int, b: Int): Double {
        val ux = lab[b * 3] - lab[a * 3]
        val uy = lab[b * 3 + 1] - lab[a * 3 + 1]
        val uz = lab[b * 3 + 2] - lab[a * 3 + 2]
        val u2 = ux * ux + uy * uy + uz * uz
        if (u2 < EDGE_SIDES * EDGE_SIDES) return Double.POSITIVE_INFINITY
        val vx = lab[c * 3] - lab[a * 3]
        val vy = lab[c * 3 + 1] - lab[a * 3 + 1]
        val vz = lab[c * 3 + 2] - lab[a * 3 + 2]
        val t = (vx * ux + vy * uy + vz * uz) / u2
        if (t < EDGE_T || t > 1 - EDGE_T) return Double.POSITIVE_INFINITY
        val px = vx - t * ux
        val py = vy - t * uy
        val pz = vz - t * uz
        val p2 = ((px * px + py * py + pz * pz) / u2).toDouble()
        return if (p2 <= EDGE_PERP * EDGE_PERP) p2 else Double.POSITIVE_INFINITY
    }

    val cnt = IntArray(k)
    val queue = IntArray(total)
    eachPart(cols, rows, label) { part, qt, c ->
        var thin = 0
        for (q in 0 until qt) thin += edge[part[q]].toInt()
        if (thin < qt * EDGE_THIN) return@eachPart
        val near = mutableListOf<Int>()
        for (q in 0 until qt) for (kk in 0 until 4) {
            val n = nb(part[q], kk)
            if (n >= 0 && label[n] != c && cnt[label[n]]++ == 0) near.add(label[n])
        }
        var a = -1
        var b = -1
        var best = Double.POSITIVE_INFINITY
        for (p in near.indices) for (r in p + 1 until near.size) {
            val d = between(c, near[p], near[r])
            if (d < best) { best = d; a = near[p]; b = near[r] }
        }
        for (l in near) cnt[l] = 0
        if (a < 0) return@eachPart
        for (q in 0 until qt) take[part[q]] = -2
        var qh = 0
        var qe = 0
        for (q in 0 until qt) {
            val i = part[q]
            for (kk in 0 until 4) {
                val n = nb(i, kk)
                if (n >= 0 && take[n] == -1 && (label[n] == a || label[n] == b)) { take[i] = label[n]; queue[qe++] = i; break }
            }
        }
        while (qh < qe) {
            val i = queue[qh++]
            for (kk in 0 until 4) {
                val n = nb(i, kk)
                if (n >= 0 && take[n] == -2) { take[n] = take[i]; queue[qe++] = n }
            }
        }
        for (q in 0 until qt) {
            val i = part[q]
            if (take[i] >= 0) label[i] = take[i]
            take[i] = -1
        }
    }
}

fun potts(cols: Int, rows: Int, label: IntArray, lab: FloatArray, pal: FloatArray, lambda: Double, iters: Int = 6) {
    if (lambda <= 0) return
    val cand = IntArray(5)
    repeat(iters) {
        var changed = 0
        for (y in 0 until rows) for (x in 0 until cols) {
            val i = y * cols + x
            val own = label[i]
            var nc = 1
            cand[0] = own
            for (kk in 0 until 4) {
                val nx = x + PX[kk]
                val ny = y + PY[kk]
                if (nx < 0 || ny < 0 || nx >= cols || ny >= rows) continue
                val l = label[ny * cols + nx]
                var j = 0
                while (j < nc && cand[j] != l) j++
                if (j == nc) cand[nc++] = l
            }
            if (nc == 1) continue
            val ll = lab[i * 3]
            val aa = lab[i * 3 + 1]
            val bb = lab[i * 3 + 2]
            var best = own
            var bc = Double.POSITIVE_INFINITY
            for (j in 0 until nc) {
                val c = cand[j]
                var cost = (ll - pal[c * 3]).toDouble() * (ll - pal[c * 3]) +
                    (aa - pal[c * 3 + 1]).toDouble() * (aa - pal[c * 3 + 1]) +
                    (bb - pal[c * 3 + 2]).toDouble() * (bb - pal[c * 3 + 2])
                for (kk in 0 until 8) {
                    val nx = x + PX[kk]
                    val ny = y + PY[kk]
                    if (nx >= 0 && ny >= 0 && nx < cols && ny < rows && label[ny * cols + nx] != c) cost += lambda * PW[kk]
                }
                if (cost < bc) { bc = cost; best = c }
            }
            if (best != own) { label[i] = best; changed++ }
        }
        if (changed == 0) return
    }
}

private fun eachPart(cols: Int, rows: Int, label: IntArray, cb: (queue: IntArray, n: Int, l: Int) -> Unit) {
    val total = cols * rows
    val seen = ByteArray(total)
    val queue = IntArray(total)
    for (s in 0 until total) {
        if (seen[s] != 0.toByte()) continue
        val l = label[s]
        var qh = 0
        var qt = 0
        seen[s] = 1
        queue[qt++] = s
        while (qh < qt) {
            val c = queue[qh++]
            val x = c % cols
            val y = (c - x) / cols
            for (kk in 0 until 8) {
                val nx = x + N8X[kk]
                val ny = y + N8Y[kk]
                if (nx < 0 || ny < 0 || nx >= cols || ny >= rows) continue
                val n = ny * cols + nx
                if (seen[n] == 0.toByte() && label[n] == l) { seen[n] = 1; queue[qt++] = n }
            }
        }
        cb(queue, qt, l)
    }
}


private const val MERGE_DREF = 0.0125
private const val MERGE_BOOST = 16.0

fun mergeSmall(cols: Int, rows: Int, label: IntArray, k: Int, minArea: Int, lab: FloatArray? = null) {
    if (minArea <= 1) return
    val total = cols * rows
    val nb = IntArray(k)
    val limit = if (lab != null) (minArea * MERGE_BOOST).toInt() else minArea

    fun delta(a: Int, b: Int): Double {
        val l = lab!!
        val dl = l[a * 3] - l[b * 3]
        val da = l[a * 3 + 1] - l[b * 3 + 1]
        val db = l[a * 3 + 2] - l[b * 3 + 2]
        return sqrt((dl * dl + da * da + db * db).toDouble())
    }

    repeat(2) {
        var changed = false
        eachPart(cols, rows, label) { queue, qt, l0 ->
            if (qt >= limit) return@eachPart
            val touchedL = mutableListOf<Int>()
            for (q in 0 until qt) {
                val c = queue[q]
                val x = c % cols
                val nbs = intArrayOf(if (x > 0) c - 1 else -1, if (x < cols - 1) c + 1 else -1, c - cols, c + cols)
                for (n in nbs) {
                    if (n !in 0..<total || label[n] == l0) continue
                    if (nb[label[n]]++ == 0) touchedL.add(label[n])
                }
            }
            var best = -1
            var bd = Double.POSITIVE_INFINITY
            for (nl in touchedL) {
                if (lab == null) {
                    if (best < 0 || nb[nl] > nb[best]) best = nl
                    continue
                }
                val d = delta(l0, nl)
                if (d < bd - 1e-9 || (d < bd + 1e-9 && nb[nl] > nb[best])) { bd = d; best = nl }
            }
            for (nl in touchedL) nb[nl] = 0
            if (best < 0) return@eachPart
            if (lab != null && qt * min(MERGE_BOOST, max(1.0 / MERGE_BOOST, bd / MERGE_DREF)) >= minArea) return@eachPart
            for (q in 0 until qt) label[queue[q]] = best
            changed = true
        }
        if (!changed) return
    }
}
fun countParts(cols: Int, rows: Int, label: IntArray, k: Int): IntArray {
    val parts = IntArray(k)
    eachPart(cols, rows, label) { _, _, l -> parts[l]++ }
    return parts
}
