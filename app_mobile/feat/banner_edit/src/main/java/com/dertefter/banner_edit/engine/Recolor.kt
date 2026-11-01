package com.dertefter.banner_edit.engine


import kotlin.math.max
import kotlin.math.sqrt


fun recolor(ops: List<Op>, targetLab: FloatArray, w: Int, h: Int): List<Op> {
    val n = w * h
    val owner = IntArray(n).apply { fill(-1) }
    val top = IntArray(n).apply { fill(-1) }
    val cv = Coverage(w, h)
    ops.forEachIndexed { k, o ->
        cv.op(o)
        for (t in 0 until cv.n) {
            val i = cv.touched[t]
            top[i] = k
            owner[i] = if (cv.cov[i] >= 0.999f) k else -1
        }
        cv.clear()
    }
    val parent = IntArray(ops.size) { it }

    fun find(k0: Int): Int {
        var k = k0
        while (parent[k] != k) {
            parent[k] = parent[parent[k]]
            k = parent[k]
        }
        return k
    }

    fun join(a: Int, b: Int) {
        if (b < 0 || a == b || ops[a].color != ops[b].color || ops[b].tool == TOOL_ERASER) return
        val ra = find(a)
        val rb = find(b)
        if (ra != rb) parent[ra] = rb
    }

    for (y in 0 until h) for (x in 0 until w) {
        val i = y * w + x
        val a = top[i]
        if (a < 0 || ops[a].tool == TOOL_ERASER) continue
        if (x + 1 < w) join(a, top[i + 1])
        if (y + 1 < h) join(a, top[i + w])
    }
    val group = IntArray(ops.size) { find(it) }
    val start = IntArray(ops.size + 1)
    for (i in 0 until n) if (owner[i] >= 0) start[group[owner[i]] + 1]++
    for (kk in ops.indices) start[kk + 1] += start[kk]
    val fill = start.copyOfRange(0, ops.size)
    val list = IntArray(start[ops.size])
    for (i in 0 until n) if (owner[i] >= 0) list[fill[group[owner[i]]]++] = i

    fun err(a: Int, b: Int, c: DoubleArray): Double {
        var s = 0.0
        for (q in a until b) {
            val j = list[q] * 3
            val dl = targetLab[j] - c[0]
            val da = targetLab[j + 1] - c[1]
            val db = targetLab[j + 2] - c[2]
            s += sqrt((dl * dl + da * da + db * db))
        }
        return s
    }

    val color = ops.map { it.color }.toMutableList()
    for (g in 0 until ops.size) {
        val a = start[g]
        val b = start[g + 1]
        if (group[g] != g || a == b || ops[g].tool == TOOL_ERASER) continue
        val hex = fromHex(ops[g].color)
        val c0 = oklab(hex[0], hex[1], hex[2])
        var l = c0[0]
        var aa = c0[1]
        var bb = c0[2]
        repeat(5) {
            var sw = 0.0
            var sl = 0.0
            var sa = 0.0
            var sb = 0.0
            for (q in a until b) {
                val j = list[q] * 3
                val dl = targetLab[j] - l
                val da = targetLab[j + 1] - aa
                val db = targetLab[j + 2] - bb
                val wt = 1 / max(0.004, sqrt((dl * dl + da * da + db * db)))
                sw += wt
                sl += targetLab[j] * wt
                sa += targetLab[j + 1] * wt
                sb += targetLab[j + 2] * wt
            }
            l = sl / sw
            aa = sa / sw
            bb = sb / sw
        }
        val newHex = toHex(fromOklab(l, aa, bb)[0], fromOklab(l, aa, bb)[1], fromOklab(l, aa, bb)[2])
        if (newHex != ops[g].color) {
            val rgb = fromHex(newHex)
            val newLab = oklab(rgb[0], rgb[1], rgb[2])
            if (err(a, b, newLab) < err(a, b, c0)) color[g] = newHex
        }
    }
    return ops.mapIndexed { k, o ->
        val c = color[group[k]]
        if (c == o.color) o else o.copy(color = c)
    }
}
