package com.dertefter.banner_edit.engine

import kotlin.math.abs
import kotlin.math.floor

class ExactOptions(
    val cell: Int,
    val abortOps: Int? = null,
    val abortPoints: Int? = null,
    val onProgress: ((done: Double) -> Unit)? = null,
)

fun exact(cols: Int, rows: Int, label: IntArray, palette: List<String>, white: Int, opt: ExactOptions): List<Op> {
    val total = cols * rows
    val cell = opt.cell
    val area = IntArray(palette.size)
    for (i in 0 until total) if (label[i] != white) area[label[i]]++
    val order = palette.indices.filter { area[it] > 0 }.sortedBy { area[it] }
    val rankOf = IntArray(palette.size).apply { fill(-1) }
    order.forEachIndexed { r, p -> rankOf[p] = r }
    val rk = IntArray(total).apply { fill(-1) }
    for (i in 0 until total) if (label[i] != white) rk[i] = rankOf[label[i]]

    val vis = IntArray(total)
    val parent = IntArray(total)
    val queue = IntArray(total)
    val keep = ByteArray(total)
    val head = IntArray(total)
    val nextS = IntArray(total)

    fun xy(i: Int): Pt = Pt((i % cols + 0.5) * cell, (floor(i.toDouble() / cols) + 0.5) * cell)

    fun later(c: Int, d: Int, l: Int): Int {
        val x = c % cols
        if ((d == 1 && x == cols - 1) || (d == -1 && x == 0)) return -1
        val n = c + d
        return if (n in 0 until total && rk[n] >= l) n else -1
    }

    fun tipExt(t: Int, d: Int, l: Int): Int {
        var n = later(t, d, l)
        val sides = if (abs(d) == 1) intArrayOf(cols, -cols) else intArrayOf(1, -1)
        for (s in sides) if (n < 0) n = later(t, s, l)
        return n
    }

    fun tipsOut(tour: List<Int>, l: Int): List<Int> {
        if (tour.size == 1) {
            val s = tour[0]
            for ((a, b) in listOf(1 to -1, cols to -cols)) {
                val u = later(s, a, l)
                val v = later(s, b, l)
                if (u >= 0 && v >= 0) return listOf(u, s, v)
            }
            val ns = intArrayOf(1, -1, cols, -cols).map { later(s, it, l) }.filter { it >= 0 }
            return when {
                ns.size >= 2 -> listOf(ns[0], s, ns[1])
                ns.size == 1 -> listOf(ns[0], s)
                else -> listOf(s)
            }
        }
        val res = mutableListOf<Int>()
        val n = tour.size
        val hd = tipExt(tour[0], tour[0] - tour[1], l)
        val tl = tipExt(tour[n - 1], tour[n - 1] - tour[n - 2], l)
        if (hd >= 0) res.add(hd)
        for (i in 0 until n) {
            res.add(tour[i])
            if (i > 0 && i < n - 1 && tour[i - 1] == tour[i + 1]) {
                val u = tipExt(tour[i], tour[i] - tour[i - 1], l)
                if (u >= 0) { res.add(u); res.add(tour[i]) }
            }
        }
        if (tl >= 0) res.add(tl)
        return res
    }

    val out = mutableListOf<Op>()
    var points = 0

    for (l in order.indices) {
        val mark = l + 1
        var qt = 0

        fun push(nb: Int, c: Int) {
            if (rk[nb] >= l && vis[nb] != mark) {
                vis[nb] = mark
                parent[nb] = c
                head[nb] = -1
                queue[qt++] = nb
            }
        }

        for (s0 in 0 until total) {
            if (rk[s0] != l || vis[s0] == mark) continue

            qt = 0
            var qh = 0
            vis[s0] = mark
            parent[s0] = -1
            head[s0] = -1
            queue[qt++] = s0
            while (qh < qt) {
                val c = queue[qh++]
                val x = c % cols
                if (x > 0) push(c - 1, c)
                if (x < cols - 1) push(c + 1, c)
                if (c >= cols) push(c - cols, c)
                if (c + cols < total) push(c + cols, c)
            }
            for (i in qt - 1 downTo 0) {
                val c = queue[i]
                if (rk[c] == l) keep[c] = 1
                if (keep[c] != 0.toByte() && parent[c] >= 0) keep[parent[c]] = 1
            }
            for (i in 1 until qt) {
                val c = queue[i]
                if (keep[c] != 0.toByte()) {
                    val p = parent[c]
                    nextS[c] = head[p]
                    head[p] = c
                }
            }
            val stack = mutableListOf(s0)
            val tour = mutableListOf(s0)
            var lastNew = 0
            while (stack.isNotEmpty()) {
                val c = stack.last()
                val ch = head[c]
                if (ch != -1) {
                    head[c] = nextS[ch]
                    stack.add(ch)
                    tour.add(ch)
                    lastNew = tour.size - 1
                } else {
                    stack.removeAt(stack.size - 1)
                    if (stack.isNotEmpty()) tour.add(stack.last())
                }
            }
            while (tour.size > lastNew + 1) tour.removeAt(tour.size - 1)
            for (i in 0 until qt) keep[queue[i]] = 0
            val path = tipsOut(tour, l)
            val pts = mutableListOf<Pt>()
            for (i in path.indices) {
                if (i > 0 && i < path.size - 1 && path[i] - path[i - 1] == path[i + 1] - path[i]) continue
                pts.add(xy(path[i]))
            }
            out.add(Op(tool = TOOL_BRUSH, color = palette[order[l]], width = cell.toDouble(), points = pts))
            points += pts.size
            if ((opt.abortOps != null && out.size > opt.abortOps) || (opt.abortPoints != null && points > opt.abortPoints)) throw Aborted()
        }
        opt.onProgress?.invoke((l + 1).toDouble() / order.size)
    }
    return out
}
