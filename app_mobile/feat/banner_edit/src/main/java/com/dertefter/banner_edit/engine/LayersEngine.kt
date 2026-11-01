package com.dertefter.banner_edit.engine

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class LayersOptions(
    val cell: Int,
    val tol: Double,
    val routePoints: Int,
    val abortOps: Int? = null,
    val abortPoints: Int? = null,
    val onProgress: ((done: Double) -> Unit)? = null,
)

class Aborted : Exception("подбор прерван: рисунок не влезает в лимиты")

private const val INF = 1e20
private const val LEVEL_STEP = 0.35
private const val WIDE_MIN_GAIN = 3.0
private const val ROUTE_VISIT_PER_POINT = 4000

private const val TOL_EPS = 0.05
private const val TOL_DEPTH = 0.5
private val TIER_A = doubleArrayOf(0.0, 0.25, 0.5)

private class Seg(val ax: Int, val ay: Int, val bx: Int, val by: Int)

private class Cand(var g: Int, val o: Int, val l: Int, var a: Int, var b: Int)

private class RouteHit(val cell: Int, val via: List<Pt>)

private class LayersCtx(
    val cols: Int,
    val rows: Int,
    val cell: Int,
    var left: Int,
    val gy: DoubleArray,
    val gr: IntArray,
    val env: DoubleArray,
    val v: IntArray,
    val z: DoubleArray,
    val d: FloatArray,
    val site: IntArray,
    val sb: FloatArray,
    val tier: IntArray,
    val td: FloatArray,
    val tSite: IntArray,
    val t: ByteArray,
    val touched: ByteArray,
    val mark: IntArray,
    val parent: IntArray,
    val queue: IntArray,
    var stamp: Int,
    val pPre: IntArray,
    val qPre: IntArray,
    val endHead: IntArray,
    val routePoints: Int,
    val routeVisit: Int,
    var bx0: Int, var bx1: Int, var by0: Int, var by1: Int,
    var wx0: Int, var wx1: Int, var wy0: Int, var wy1: Int,
    var reach: Int,
    val lab: FloatArray,
    val label: IntArray,
    val eps: Double,
    val depth: Double,
    var cur: Int,
    var dWhite: Double,
    val under: IntArray,
    val under0: IntArray,
    val claimed: MutableList<Int>,
    val cov: FloatArray,
    val covStamp: IntArray,
    var covAt: Int,
)

private fun delta(ctx: LayersCtx, a: Int, b: Int): Double {
    val lab = ctx.lab
    val kk = lab.size / 3 - 1
    val i = (if (a < 0) kk else a) * 3
    val j = (if (b < 0) kk else b) * 3
    val dl = lab[i] - lab[j]
    val da = lab[i + 1] - lab[j + 1]
    val db = lab[i + 2] - lab[j + 2]
    return sqrt((dl * dl + da * da + db * db).toDouble())
}

private fun edt(ctx: LayersCtx, tt: Int) {
    val cols = ctx.cols
    val gy = ctx.gy
    val gr = ctx.gr
    val env = ctx.env
    val v = ctx.v
    val z = ctx.z
    val tier = ctx.tier
    val td = ctx.td
    val tSite = ctx.tSite
    for (x in ctx.wx0..ctx.wx1) {
        var last = -1
        for (y in ctx.wy0..ctx.wy1) {
            val i = y * cols + x
            if (tier[i] == tt) last = y
            gy[i] = if (last < 0) INF else (y - last).toDouble()
            gr[i] = last
        }
        last = -1
        for (y in ctx.wy1 downTo ctx.wy0) {
            val i = y * cols + x
            if (tier[i] == tt) last = y
            if (last >= 0 && last - y < gy[i]) {
                gy[i] = (last - y).toDouble()
                gr[i] = last
            }
        }
    }
    for (y in ctx.wy0..ctx.wy1) {
        val o = y * cols
        var k = -1
        for (q in ctx.wx0..ctx.wx1) {
            val g = gy[o + q]
            if (g >= INF) continue
            val fq = g * g + q * q
            var s = 0.0
            while (k >= 0) {
                s = (fq - env[k]) / (2 * (q - v[k]))
                if (s <= z[k]) k-- else break
            }
            k++
            v[k] = q
            env[k] = fq
            z[k] = if (k != 0) s else Double.NEGATIVE_INFINITY
        }
        if (k < 0) {
            for (x in ctx.wx0..ctx.wx1) {
                td[o + x] = Float.POSITIVE_INFINITY
                tSite[o + x] = -1
            }
            continue
        }
        var j = 0
        for (x in ctx.wx0..ctx.wx1) {
            while (j < k && z[j + 1] < x) j++
            val p = v[j]
            val g = gy[o + p]
            td[o + x] = sqrt(((x - p) * (x - p) + g * g)).toFloat()
            tSite[o + x] = gr[o + p] * cols + p
        }
    }
}

private fun distanceField(ctx: LayersCtx, tiers: List<Int>): Double {
    val cols = ctx.cols
    val d = ctx.d
    val site = ctx.site
    val sb = ctx.sb
    for (y in ctx.wy0..ctx.wy1) for (x in ctx.wx0..ctx.wx1) {
        val i = y * cols + x
        d[i] = Float.POSITIVE_INFINITY
        site[i] = -1
        sb[i] = 0f
    }
    for (t in tiers) {
        edt(ctx, t)
        val a = TIER_A[t - 1]
        for (y in ctx.wy0..ctx.wy1) for (x in ctx.wx0..ctx.wx1) {
            val i = y * cols + x
            val e = ctx.td[i] + a
            if (e < d[i]) {
                d[i] = e.toFloat()
                site[i] = ctx.tSite[i]
                sb[i] = a.toFloat()
            }
        }
    }
    var maxD = 0.0
    val x0 = max(ctx.wx0, ctx.bx0 - ctx.reach)
    val x1 = min(ctx.wx1, ctx.bx1 + ctx.reach)
    val yA = max(ctx.wy0, ctx.by0 - ctx.reach)
    val yB = min(ctx.wy1, ctx.by1 + ctx.reach)
    for (y in yA..yB) {
        var i = y * cols + x0
        val e = y * cols + x1
        while (i <= e) {
            if (d[i] > maxD) maxD = d[i].toDouble()
            i++
        }
    }
    return maxD
}

private val NX = intArrayOf(0, 1, -1, 0, 0)
private val NY = intArrayOf(0, 0, 0, 1, -1)

private fun forb2(ctx: LayersCtx, px: Double, py: Double): Double {
    val cols = ctx.cols
    val rows = ctx.rows
    val site = ctx.site
    val sb = ctx.sb
    val cx = floor(px + 0.5).toInt()
    val cy = floor(py + 0.5).toInt()
    var best = Double.POSITIVE_INFINITY
    for (k in 0 until 5) {
        val nx = cx + NX[k]
        val ny = cy + NY[k]
        if (nx < 0 || ny < 0 || nx >= cols || ny >= rows) continue
        val n = ny * cols + nx
        val s = site[n]
        if (s < 0) continue
        val sx = s % cols
        val dx = px - sx
        val dy = py - (s - sx) / cols
        best = min(best, hypot(dx, dy) + sb[n].toDouble())
    }
    return best
}

private fun safeSeg(ctx: LayersCtx, ax: Int, ay: Int, bx: Int, by: Int, rr: Double): Boolean {
    if (rr <= 0) return true
    val cols = ctx.cols
    val d = ctx.d
    val len = hypot((bx - ax).toDouble(), (by - ay).toDouble())
    if ((d[ay * cols + ax] + d[by * cols + bx] - len) / 2 >= rr) return true
    val n = ceil(len * 2).toInt()
    val lim = rr - 1e-6
    for (i in 0..n) {
        val t = if (n != 0) i.toDouble() / n else 0.0
        if (forb2(ctx, ax + (bx - ax) * t, ay + (by - ay) * t) < lim) return false
    }
    return true
}

private fun segDist2(px: Double, py: Double, ax: Double, ay: Double, bx: Double, by: Double): Double {
    val dx = bx - ax
    val dy = by - ay
    val l2 = dx * dx + dy * dy
    var t = if (l2 != 0.0) ((px - ax) * dx + (py - ay) * dy) / l2 else 0.0
    t = if (t < 0) 0.0 else if (t > 1) 1.0 else t
    val ex = ax + t * dx - px
    val ey = ay + t * dy - py
    return ex * ex + ey * ey
}

private fun claimSeg(ctx: LayersCtx, ax: Int, ay: Int, bx: Int, by: Int, covR: Double, apply: Boolean): Int {
    val cols = ctx.cols
    val rows = ctx.rows
    val t = ctx.t
    val c2 = covR * covR + 1e-6
    val e = ceil(covR).toInt()
    val x0 = max(0, min(ax, bx) - e)
    val x1 = min(cols - 1, max(ax, bx) + e)
    val y0 = max(0, min(ay, by) - e)
    val y1 = min(rows - 1, max(ay, by) + e)
    var n = 0
    for (y in y0..y1) for (x in x0..x1) {
        val i = y * cols + x
        if (t[i] == 0.toByte() || segDist2(x.toDouble(), y.toDouble(), ax.toDouble(), ay.toDouble(), bx.toDouble(), by.toDouble()) > c2) continue
        n++
        if (apply) {
            t[i] = 0
            ctx.left--
            ctx.claimed.add(i)
            ctx.under0[i] = ctx.under[i]
        }
    }
    return n
}

private fun paintSeg(ctx: LayersCtx, ax: Int, ay: Int, bx: Int, by: Int, m: Int) {
    val cols = ctx.cols
    val rows = ctx.rows
    val t = ctx.t
    val touched = ctx.touched
    val under = ctx.under
    val cov = ctx.cov
    val covStamp = ctx.covStamp
    val covAt = ctx.covAt
    val cell = ctx.cell
    val r = m + 1 + 0.71 * (1 - 1.0 / cell)
    val e = ceil(r).toInt()
    val c2 = (m * m).toDouble()
    val x0 = max(0, min(ax, bx) - e)
    val x1 = min(cols - 1, max(ax, bx) + e)
    val y0 = max(0, min(ay, by) - e)
    val y1 = min(rows - 1, max(ay, by) + e)
    for (y in y0..y1) for (x in x0..x1) {
        val d2 = segDist2(x.toDouble(), y.toDouble(), ax.toDouble(), ay.toDouble(), bx.toDouble(), by.toDouble())
        if (d2 >= r * r) continue
        val i = y * cols + x
        val c = min(1.0, max(0.0, (m + 0.5 - sqrt(d2)) * cell + 0.5))
        if (t[i] != 0.toByte() && d2 <= c2) {
            t[i] = 0
            ctx.left--
        }
        if (covStamp[i] != covAt) {
            covStamp[i] = covAt
            cov[i] = c.toFloat()
        } else if (c > cov[i]) {
            cov[i] = c.toFloat()
        }
        if (c >= 0.5) under[i] = ctx.cur
        if (c * ctx.dWhite > ctx.eps) touched[i] = 1
    }
}

private fun restoreUncovered(ctx: LayersCtx) {
    val claimed = ctx.claimed
    for (i in claimed) {
        val c = if (ctx.covStamp[i] == ctx.covAt) ctx.cov[i].toDouble() else 0.0
        if (c < 0.999 && (1 - c) * delta(ctx, ctx.cur, ctx.under0[i]) > ctx.eps) {
            ctx.t[i] = 1
            ctx.left++
        }
    }
    claimed.clear()
}

private fun heapPush(heap: MutableList<Cand>, c: Cand) {
    heap.add(c)
    var i = heap.size - 1
    while (i != 0) {
        val p = (i - 1) shr 1
        if (heap[p].g >= heap[i].g) break
        val tmp = heap[p]
        heap[p] = heap[i]
        heap[i] = tmp
        i = p
    }
}

private fun heapPop(heap: MutableList<Cand>): Cand {
    val top = heap[0]
    val last = heap.removeAt(heap.size - 1)
    if (heap.isNotEmpty()) {
        heap[0] = last
        var i = 0
        while (true) {
            val l = 2 * i + 1
            val rr = l + 1
            var b = i
            if (l < heap.size && heap[l].g > heap[b].g) b = l
            if (rr < heap.size && heap[rr].g > heap[b].g) b = rr
            if (b == i) break
            val tmp = heap[b]
            heap[b] = heap[i]
            heap[i] = tmp
            i = b
        }
    }
    return top
}

private fun levelSegments(ctx: LayersCtx, m: Int): List<Seg> {
    val cols = ctx.cols
    val rows = ctx.rows
    val d = ctx.d
    val t = ctx.t
    val pPre = ctx.pPre
    val qPre = ctx.qPre
    val bw = 2 * m + 1
    val thr = m + 1
    val st = max(1, bw shr 2)
    val minGain = if (m != 0) WIDE_MIN_GAIN * bw else 1.0
    val x0 = max(0, ctx.bx0 - m)
    val x1 = min(cols - 1, ctx.bx1 + m)
    val y0 = max(0, ctx.by0 - m)
    val y1 = min(rows - 1, ctx.by1 + m)
    for (x in x0..x1) pPre[x * (rows + 1) + y0] = 0
    for (y in y0..y1) qPre[y * (cols + 1) + x0] = 0
    for (y in y0..y1) for (x in x0..x1) {
        val tv = t[y * cols + x]
        pPre[x * (rows + 1) + y + 1] = pPre[x * (rows + 1) + y] + tv
        qPre[y * (cols + 1) + x + 1] = qPre[y * (cols + 1) + x] + tv
    }
    val heap = mutableListOf<Cand>()
    for (o in 0..1) {
        val lines = if (o != 0) cols else rows
        val lineStep = if (o != 0) 1 else cols
        val stride = if (o != 0) cols else 1
        val pre = if (o != 0) qPre else pPre
        val l0 = if (o != 0) x0 else y0
        val l1 = if (o != 0) x1 else y1
        val a0w = if (o != 0) y0 else x0
        val a1w = if (o != 0) y1 else x1
        val list = mutableListOf<Int>()
        var l = l0
        while (l <= l1) {
            list.add(l)
            l += st
        }
        if (list.last() != l1) list.add(l1)
        for (lv in list) {
            val lo = max(l0, lv - m)
            val hi = min(l1 + 1, lv + m + 1)
            var a = a0w
            while (a <= a1w) {
                if (d[lv * lineStep + a * stride] < thr) {
                    a++
                    continue
                }
                val a0 = a
                var g = 0
                while (a <= a1w && d[lv * lineStep + a * stride] >= thr) {
                    g += pre[a * (lines + 1) + hi] - pre[a * (lines + 1) + lo]
                    a++
                }
                if (g >= minGain) heapPush(heap, Cand(g, o, lv, a0, a - 1))
            }
        }
    }

    fun has(o: Int, l: Int, a: Int): Boolean {
        val hi = min((if (o != 0) cols else rows) - 1, l + m)
        for (k in max(0, l - m)..hi) {
            if (t[if (o != 0) a * cols + k else k * cols + a] != 0.toByte()) return true
        }
        return false
    }

    val segs = mutableListOf<Seg>()
    while (heap.isNotEmpty()) {
        val c = heapPop(heap)
        var s = c.a
        var e = c.b
        while (s <= e && !has(c.o, c.l, s)) s++
        if (s > e) continue
        while (!has(c.o, c.l, e)) e--
        val ax: Int; val ay: Int; val bx: Int; val by: Int
        if (c.o != 0) { ax = c.l; ay = s; bx = c.l; by = e } else { ax = s; ay = c.l; bx = e; by = c.l }
        val g = claimSeg(ctx, ax, ay, bx, by, m.toDouble(), false)
        if (g < minGain) continue
        if (heap.isNotEmpty() && g < heap[0].g) {
            c.a = s
            c.b = e
            c.g = g
            heapPush(heap, c)
            continue
        }
        claimSeg(ctx, ax, ay, bx, by, m.toDouble(), true)
        segs.add(Seg(ax, ay, bx, by))
    }
    return segs
}

private fun route(ctx: LayersCtx, sx: Int, sy: Int, ends: Ends, used: ByteArray, rr: Double): RouteHit? {
    val cols = ctx.cols
    val d = ctx.d
    val mark = ctx.mark
    val parent = ctx.parent
    val queue = ctx.queue
    val stamp = ++ctx.stamp
    val start = sy * cols + sx
    val rx0 = max(0, ctx.bx0 - ctx.reach)
    val rx1 = min(cols - 1, ctx.bx1 + ctx.reach)
    val ry0 = max(0, ctx.by0 - ctx.reach)
    val ry1 = min(ctx.rows - 1, ctx.by1 + ctx.reach)
    var qh = 0
    var qt = 0
    var hit = -1
    mark[start] = stamp
    parent[start] = -1
    queue[qt++] = start

    fun visit(n: Int, c: Int) {
        if (mark[n] != stamp && d[n] >= rr) {
            mark[n] = stamp
            parent[n] = c
            queue[qt++] = n
        }
    }

    while (qh < qt && qt < ctx.routeVisit) {
        val c = queue[qh++]
        if (c != start && ends.free(c, used)) {
            hit = c
            break
        }
        val x = c % cols
        val y = (c - x) / cols
        if (x > rx0) visit(c - 1, c)
        if (x < rx1) visit(c + 1, c)
        if (y > ry0) visit(c - cols, c)
        if (y < ry1) visit(c + cols, c)
    }
    if (hit < 0) return null
    val cells = mutableListOf<Pt>()
    var cur = hit
    while (cur >= 0) {
        val x = cur % cols
        cells.add(Pt(x.toDouble(), ((cur - x) / cols).toDouble()))
        cur = parent[cur]
    }
    cells.reverse()
    val via = mutableListOf<Pt>()
    var i = 0
    while (i < cells.size - 1) {
        var j = i + 1
        var k = i + 2
        while (k < cells.size) {
            if (safeSeg(ctx, cells[i].x.toInt(), cells[i].y.toInt(), cells[k].x.toInt(), cells[k].y.toInt(), rr)) j = k
            k++
        }
        if (j < cells.size - 1) via.add(cells[j])
        if (via.size > ctx.routePoints) return null
        i = j
    }
    return RouteHit(hit, via)
}

private class Ends(val head: IntArray) {
    val id = mutableListOf<Int>()
    val next = mutableListOf<Int>()
    val cells = mutableListOf<Int>()

    fun add(cell: Int, i: Int) {
        if (head[cell] < 0) cells.add(cell)
        id.add(i)
        next.add(head[cell])
        head[cell] = id.size - 1
    }

    fun first(cell: Int, used: ByteArray): Int {
        var k = head[cell]
        while (k >= 0) {
            if (used[id[k]] == 0.toByte()) return id[k]
            k = next[k]
        }
        return -1
    }

    fun free(cell: Int, used: ByteArray): Boolean = head[cell] >= 0 && first(cell, used) >= 0

    fun reset() {
        for (c in cells) head[c] = -1
    }
}

private fun chainSegments(ctx: LayersCtx, segs: List<Seg>, rr: Double, bw: Int): List<List<Pt>> {
    val bs = max(8, 2 * bw)
    val gx = ceil(ctx.cols.toDouble() / bs).toInt() + 1
    val grid = HashMap<Int, MutableList<Int>>()
    val ends = Ends(ctx.endHead)
    segs.forEachIndexed { id, s ->
        ends.add(s.ay * ctx.cols + s.ax, id)
        ends.add(s.by * ctx.cols + s.bx, id)
    }

    fun put(x: Int, y: Int, id: Int) {
        val k = (y / bs) * gx + (x / bs)
        val a = grid.getOrPut(k) { mutableListOf() }
        a.add(id)
    }

    segs.forEachIndexed { id, s ->
        put(s.ax, s.ay, id)
        put(s.bx, s.by, id)
    }
    val used = ByteArray(segs.size)
    val order = segs.indices.sortedWith(compareBy({ segs[it].ay }, { segs[it].ax }))

    fun extend(path: MutableList<Pt>) {
        outer@ while (true) {
            val tipPt = path[path.size - 1]
            val cx = tipPt.x.toInt()
            val cy = tipPt.y.toInt()
            val bx0 = cx / bs
            val by0 = cy / bs
            val cand = mutableListOf<Triple<Double, Int, Int>>()
            for (dy in -1..1) for (dx in -1..1) {
                val a = grid[(by0 + dy) * gx + bx0 + dx]
                if (a != null) for (id in a) if (used[id] == 0.toByte()) {
                    val s = segs[id]
                    val d0 = (s.ax - cx) * (s.ax - cx).toDouble() + (s.ay - cy) * (s.ay - cy).toDouble()
                    val d1 = (s.bx - cx) * (s.bx - cx).toDouble() + (s.by - cy) * (s.by - cy).toDouble()
                    if (d0 <= d1) cand.add(Triple(d0, id, 0)) else cand.add(Triple(d1, id, 1))
                }
            }
            cand.sortBy { it.first }
            var linked = false
            var t = 0
            var tries = 0
            while (t < cand.size && tries < 8) {
                val (_, id, rev) = cand[t]
                t++
                if (used[id] != 0.toByte()) continue
                tries++
                val s = segs[id]
                val p = if (rev != 0) Pt(s.bx.toDouble(), s.by.toDouble()) else Pt(s.ax.toDouble(), s.ay.toDouble())
                val q = if (rev != 0) Pt(s.ax.toDouble(), s.ay.toDouble()) else Pt(s.bx.toDouble(), s.by.toDouble())
                if (!safeSeg(ctx, cx, cy, p.x.toInt(), p.y.toInt(), rr)) continue
                used[id] = 1
                path.add(p)
                path.add(q)
                linked = true
                break
            }
            if (linked) continue
            val r = route(ctx, cx, cy, ends, used, rr) ?: return
            val id = ends.first(r.cell, used)
            val s = segs[id]
            val rev = s.ax + s.ay * ctx.cols != r.cell
            used[id] = 1
            path.addAll(r.via)
            if (rev) {
                path.add(Pt(s.bx.toDouble(), s.by.toDouble()))
                path.add(Pt(s.ax.toDouble(), s.ay.toDouble()))
            } else {
                path.add(Pt(s.ax.toDouble(), s.ay.toDouble()))
                path.add(Pt(s.bx.toDouble(), s.by.toDouble()))
            }
        }
    }

    val paths = mutableListOf<List<Pt>>()
    for (start in order) {
        if (used[start] != 0.toByte()) continue
        used[start] = 1
        val s = segs[start]
        val path = mutableListOf(Pt(s.ax.toDouble(), s.ay.toDouble()), Pt(s.bx.toDouble(), s.by.toDouble()))
        extend(path)
        path.reverse()
        extend(path)
        paths.add(path.toList())
    }
    ends.reset()
    return paths
}
private fun simplify(ctx: LayersCtx, pts: List<Pt>, rr: Double, dev: Double): List<Pt> {
    val p = mutableListOf<Pt>()
    for (q in pts) {
        val l = p.lastOrNull()
        if (l == null || l.x != q.x || l.y != q.y) p.add(q)
    }
    if (p.size <= 2) return p
    val out = mutableListOf(p[0])
    val d2 = dev * dev + 1e-9
    var i = 0
    while (i < p.size - 1) {
        var j = i + 1
        var k = i + 2
        var broke = false
        while (k < p.size && !broke && k <= i + 96) {
            var okk = true
            var q = i + 1
            while (q < k && okk) {
                okk = segDist2(p[q].x, p[q].y, p[i].x, p[i].y, p[k].x, p[k].y) <= d2
                q++
            }
            if (!okk || !safeSeg(ctx, p[i].x.toInt(), p[i].y.toInt(), p[k].x.toInt(), p[k].y.toInt(), rr)) broke = true
            else { j = k; k++ }
        }
        out.add(p[j])
        i = j
    }
    return out
}

private fun extendTips(ctx: LayersCtx, pts: List<Pt>): List<Pt> {
    val cols = ctx.cols
    val rows = ctx.rows
    val d = ctx.d

    fun ok(x: Int, y: Int, fx: Int, fy: Int): Boolean =
        x >= 0 && y >= 0 && x < cols && y < rows && d[y * cols + x] >= 1f && safeSeg(ctx, fx, fy, x, y, 1.0)

    if (pts.size == 1) {
        val x = pts[0].x.toInt()
        val y = pts[0].y.toInt()
        for ((ax, ay) in listOf(1 to 0, 0 to 1)) {
            if (ok(x + ax, y + ay, x, y) && ok(x - ax, y - ay, x, y)) {
                return listOf(Pt((x + ax).toDouble(), (y + ay).toDouble()), Pt(x.toDouble(), y.toDouble()), Pt((x - ax).toDouble(), (y - ay).toDouble()))
            }
        }
        for ((ax, ay) in listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)) {
            if (ok(x + ax, y + ay, x, y)) return listOf(Pt((x + ax).toDouble(), (y + ay).toDouble()), Pt(x.toDouble(), y.toDouble()))
        }
        return pts
    }
    val out = pts.toMutableList()

    fun tip(t: Pt, prev: Pt): Pt? {
        val dx = t.x - prev.x
        val dy = t.y - prev.y
        val l = max(abs(dx), abs(dy))
        val e = Pt(t.x + floor(dx / l + 0.5), t.y + floor(dy / l + 0.5))
        return if (ok(e.x.toInt(), e.y.toInt(), t.x.toInt(), t.y.toInt())) e else null
    }

    val head = tip(out[0], out[1])
    val tail = tip(out[out.size - 1], out[out.size - 2])
    if (head != null) out.add(0, head)
    if (tail != null) out.add(tail)
    return out
}

private fun dotWide(ctx: LayersCtx, x: Int, y: Int): Boolean {
    val cols = ctx.cols
    val rows = ctx.rows
    val d = ctx.d
    val label = ctx.label
    val i = y * cols + x
    val hole = (1 - PI / 4) * delta(ctx, ctx.cur, ctx.under[i])
    var bleed = 0.0
    for (nb in listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)) {
        val nx = x + nb.first
        val ny = y + nb.second
        val n = ny * cols + nx
        if (nx >= 0 && ny >= 0 && nx < cols && ny < rows && d[n] < 1f) bleed += 0.143 * delta(ctx, ctx.cur, label[n])
    }
    return bleed < hole
}

private fun toOp(color: String, m: Int, cell: Int, pts: List<Pt>): Op {
    fun px(v: Double): Double = (v + 0.5) * cell
    val out = mutableListOf<Pt>()
    for (p in pts) {
        val q = Pt(px(p.x), px(p.y))
        val l = out.lastOrNull()
        if (l == null || l.x != q.x || l.y != q.y) out.add(q)
    }
    return Op(tool = TOOL_BRUSH, color = color, width = (2 * m + 1) * cell.toDouble(), points = out)
}


fun layers(cols: Int, rows: Int, label: IntArray, palette: List<String>, white: Int, opt: LayersOptions): List<Op> {
    val totalN = cols * rows
    val kCount = palette.size
    val tol = opt.tol
    val cell = opt.cell
    val area = IntArray(kCount)
    val parts = countParts(cols, rows, label, kCount)
    for (i in 0 until totalN) area[label[i]]++
    var order = (0 until kCount).filter { area[it] > 0 }.sortedByDescending { area[it] }.toMutableList()
    val rest = order.drop(1).sortedWith { a, b -> compareValues(area[b] * sqrt(parts[b].toDouble()), area[a] * sqrt(parts[a].toDouble())) }
    if (order.size > 1) {
        order = ArrayList(order.take(1))
        order.addAll(rest)
    }

    val lab = FloatArray((kCount + 1) * 3)
    palette.forEachIndexed { kk, hex ->
        val rgb = fromHex(hex)
        val l = oklab(rgb[0], rgb[1], rgb[2])
        lab[kk * 3] = l[0].toFloat()
        lab[kk * 3 + 1] = l[1].toFloat()
        lab[kk * 3 + 2] = l[2].toFloat()
    }
    val wht = oklab(255, 255, 255)
    lab[kCount * 3] = wht[0].toFloat()
    lab[kCount * 3 + 1] = wht[1].toFloat()
    lab[kCount * 3 + 2] = wht[2].toFloat()

    val ctx = LayersCtx(
        cols = cols, rows = rows, cell = cell, left = 0,
        gy = DoubleArray(totalN), gr = IntArray(totalN), env = DoubleArray(cols), v = IntArray(cols), z = DoubleArray(cols + 1),
        d = FloatArray(totalN), site = IntArray(totalN), sb = FloatArray(totalN),
        tier = IntArray(totalN), td = FloatArray(totalN), tSite = IntArray(totalN),
        t = ByteArray(totalN), touched = ByteArray(totalN),
        mark = IntArray(totalN), parent = IntArray(totalN), queue = IntArray(totalN), stamp = 0,
        pPre = IntArray(cols * (rows + 1)), qPre = IntArray(rows * (cols + 1)), endHead = IntArray(totalN).apply { fill(-1) },
        routePoints = opt.routePoints, routeVisit = min(totalN, max(6000, opt.routePoints * ROUTE_VISIT_PER_POINT)),
        bx0 = 0, bx1 = 0, by0 = 0, by1 = 0, wx0 = 0, wx1 = 0, wy0 = 0, wy1 = 0, reach = 0,
        lab = lab, label = label,
        eps = TOL_EPS * tol, depth = TOL_DEPTH * tol, cur = 0, dWhite = 0.0,
        under = IntArray(totalN).apply { fill(-1) }, under0 = IntArray(totalN), claimed = mutableListOf(),
        cov = FloatArray(totalN), covStamp = IntArray(totalN), covAt = 0,
    )
    val slack = 0.8 * tol
    val levels = mutableListOf<Int>()
    var mm = floor((LIMITS.MAX_WIDTH / cell - 1) / 2).toInt()
    while (mm >= 1) {
        levels.add(mm)
        mm = floor(mm * LEVEL_STEP).toInt()
    }
    levels.add(0)
    ctx.reach = levels[0] + 1
    val margin = 2 * ctx.reach + 2

    val forb = ByteArray(totalN)
    val tierOf = IntArray(kCount)
    val ops = mutableListOf<Op>()
    var points = 0

    val runLevel: (Int, Int, Double) -> Unit = runLevel@{ c, m, slackV ->
        ctx.claimed.clear()
        val segs = levelSegments(ctx, m)
        if (segs.isEmpty()) return@runLevel
        val thr = (m + 1).toDouble()
        ctx.covAt++
        for (path in chainSegments(ctx, segs, thr, 2 * m + 1)) {
            var pts = simplify(ctx, path, thr, slackV)
            var wide = false
            if (m == 0) {
                pts = extendTips(ctx, pts)
                wide = pts.size == 1 && dotWide(ctx, pts[0].x.toInt(), pts[0].y.toInt())
            }
            for (i in pts.indices) {
                val a = pts[i]
                val b = pts[minOf(i + 1, pts.size - 1)]
                paintSeg(ctx, a.x.toInt(), a.y.toInt(), b.x.toInt(), b.y.toInt(), m)
            }
            val op = toOp(palette[c], m, cell, pts)
            if (wide) op.width = floor(sqrt(2.0) * cell * 100 + 0.5) / 100.0
            ops.add(op)
            points += op.points.size
        }
        restoreUncovered(ctx)
        if ((opt.abortOps != null && ops.size > opt.abortOps) || (opt.abortPoints != null && points > opt.abortPoints)) throw Aborted()
    }

    order.forEachIndexed { ci, c ->
        var left = 0
        var bx0 = cols
        var bx1 = -1
        var by0 = rows
        var by1 = -1
        var i = 0
        for (y in 0 until rows) for (x in 0 until cols) {
            val tv = if (label[i] == c && (c != white || ctx.touched[i] != 0.toByte())) 1.toByte() else 0.toByte()
            ctx.t[i] = tv
            if (tv != 0.toByte()) {
                left++
                if (x < bx0) bx0 = x
                if (x > bx1) bx1 = x
                if (y < by0) by0 = y
                if (y > by1) by1 = y
            }
            i++
        }
        if (left != 0) {
            ctx.left = left
            ctx.cur = c
            ctx.dWhite = delta(ctx, c, -1)
            ctx.bx0 = bx0; ctx.bx1 = bx1; ctx.by0 = by0; ctx.by1 = by1
            ctx.wx0 = max(0, bx0 - margin); ctx.wx1 = min(cols - 1, bx1 + margin)
            ctx.wy0 = max(0, by0 - margin); ctx.wy1 = min(rows - 1, by1 + margin)
            for (kk in 0 until kCount) {
                val dd = delta(ctx, c, kk)
                val a = if (dd > 1e-9) min(ctx.depth, ctx.eps / dd) else ctx.depth
                var t = 0
                while (t + 1 < TIER_A.size && TIER_A[t + 1] <= a + 1e-9) t++
                tierOf[kk] = t + 1
            }
            val present = mutableListOf<Int>()
            val presentFlag = BooleanArray(TIER_A.size + 1)
            for (y in ctx.wy0..ctx.wy1) for (x in ctx.wx0..ctx.wx1) {
                val idx = y * cols + x
                val t = if (forb[idx] != 0.toByte()) tierOf[label[idx]] else 0
                ctx.tier[idx] = t
                if (t != 0 && !presentFlag[t]) {
                    presentFlag[t] = true
                    present.add(t)
                }
            }
            val maxD = distanceField(ctx, present)
            for (m in levels) {
                if (ctx.left == 0) break
                if (m != 0 && maxD < m + 1) continue
                runLevel(c, m, slack)
            }
            var pass = 0
            while (ctx.left > 0 && pass < 2) {
                runLevel(c, 0, 0.0)
                pass++
            }
        }
        for (q in 0 until totalN) if (label[q] == c) forb[q] = 1
        opt.onProgress?.invoke((ci + 1).toDouble() / order.size)
    }
    return ops
}
