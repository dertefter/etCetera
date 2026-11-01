package com.dertefter.banner_edit.engine

import kotlin.math.hypot

enum class PaletteMode { VIVID, CLASSIC }

class Quantized(
    val label: IntArray,
    val palette: List<String>,
    val white: Int,
)

private const val BINS_PER_COLOR = 8

private const val VIVID_HUE = 1.4
private const val VIVID_SAT = 3.0

private fun vividColor(lSum: Double, aSum: Double, bSum: Double, cSum: Double, n: Double): String {
    val a = aSum / n
    val b = bSum / n
    val c = hypot(a, b)
    val k = if (c > 1e-6) cSum / n / c else 1.0
    val (r, g, bl) = fromOklab(lSum / n, a * k, b * k)
    return toHex(r, g, bl)
}

private class VividGroups(val group: IntArray, val k: Int)

private fun vividLabels(keys: IntArray, cnt: DoubleArray, lab: Array<DoubleArray>, colors: Int): VividGroups {
    val n = keys.size
    val x = DoubleArray(n * 3)
    val wt = DoubleArray(n)
    val dist = DoubleArray(n)
    for (i in 0 until n) {
        val k = keys[i]
        val c = cnt[k]
        val a = lab[1][k] / c
        val b = lab[2][k] / c
        x[i * 3] = lab[0][k] / c
        x[i * 3 + 1] = a * VIVID_HUE
        x[i * 3 + 2] = b * VIVID_HUE
        wt[i] = c * (1 + VIVID_SAT * hypot(a, b))
    }

    fun d2(i: Int, centers: DoubleArray, j: Int): Double {
        val dl = x[i * 3] - centers[j * 3]
        val da = x[i * 3 + 1] - centers[j * 3 + 1]
        val db = x[i * 3 + 2] - centers[j * 3 + 2]
        return dl * dl + da * da + db * db
    }

    val centers = DoubleArray(colors * 3)
    var kn = 0
    var pick = 0
    for (i in 1 until n) if (wt[i] > wt[pick]) pick = i
    dist.fill(Double.POSITIVE_INFINITY)
    while (kn < colors) {
        for (a in 0 until 3) centers[kn * 3 + a] = x[pick * 3 + a]
        kn++
        var bv = 0.0
        for (i in 0 until n) {
            val d = d2(i, centers, kn - 1)
            if (d < dist[i]) dist[i] = d
            if (dist[i] * wt[i] > bv) { bv = dist[i] * wt[i]; pick = i }
        }
        if (bv <= 0) break
    }
    val group = IntArray(n)
    val acc = DoubleArray(kn * 4)
    for (it in 0 until 10) {
        acc.fill(0.0)
        var moved = 0
        for (i in 0 until n) {
            var best = 0
            var bd = Double.POSITIVE_INFINITY
            for (j in 0 until kn) {
                val d = d2(i, centers, j)
                if (d < bd) { bd = d; best = j }
            }
            if (group[i] != best) { group[i] = best; moved++ }
            val w = wt[i]
            acc[best * 4] += x[i * 3] * w
            acc[best * 4 + 1] += x[i * 3 + 1] * w
            acc[best * 4 + 2] += x[i * 3 + 2] * w
            acc[best * 4 + 3] += w
        }
        for (j in 0 until kn) {
            if (acc[j * 4 + 3] != 0.0) {
                for (a in 0 until 3) centers[j * 3 + a] = acc[j * 4 + a] / acc[j * 4 + 3]
            }
        }
        if (it != 0 && moved == 0) break
    }
    return VividGroups(group, kn)
}

private class CutBucket(val list: IntArray, val axis: Int, val score: Double)

private fun medianCut(keys: IntArray, cnt: DoubleArray, s: Array<DoubleArray>, n: Int): List<DoubleArray> {
    fun analyse(list: IntArray): CutBucket {
        var c = 0.0
        val s0 = DoubleArray(3)
        val q = DoubleArray(3)
        for (k in list) {
            c += cnt[k]
            for (a in 0 until 3) {
                s0[a] += s[a][k]
                q[a] += s[a][k] * s[a][k] / cnt[k]
            }
        }
        val sse = DoubleArray(3) { q[it] - s0[it] * s0[it] / c }
        var axis = 0
        for (a in 1 until 3) if (sse[a] > sse[axis]) axis = a
        return CutBucket(list, axis, if (list.size > 1) sse[axis] else -1.0)
    }

    val buckets = mutableListOf(analyse(keys))
    while (buckets.size < n) {
        var bi = 0
        for (i in 1 until buckets.size) if (buckets[i].score > buckets[bi].score) bi = i
        val b = buckets[bi]
        if (b.score <= 0) break
        val a = s[b.axis]
        val sorted = b.list.toList().sortedBy { a[it] / cnt[it] }.toIntArray()
        var total = 0.0
        for (k in sorted) total += cnt[k]
        var acc = 0.0
        var cut = 1
        for (i in 0 until sorted.size - 1) {
            acc += cnt[sorted[i]]
            cut = i + 1
            if (acc >= total / 2) break
        }
        buckets[bi] = analyse(sorted.copyOfRange(0, cut))
        buckets.add(bi + 1, analyse(sorted.copyOfRange(cut, sorted.size)))
    }
    return buckets.map { b ->
        var c = 0.0
        var x = 0.0
        var y = 0.0
        var z = 0.0
        for (k in b.list) {
            c += cnt[k]
            x += s[0][k]
            y += s[1][k]
            z += s[2][k]
        }
        doubleArrayOf(x / c, y / c, z / c)
    }
}

private class Histogram(
    val bin: IntArray,
    val whites: Int,
    val cnt: DoubleArray,
    val sr: DoubleArray,
    val sg: DoubleArray,
    val sb: DoubleArray,
)

private fun histogram(data: IntArray, total: Int, whiteMin: Int, bits: Int): Histogram {
    val sh = 8 - bits
    val dense = if (bits <= 6) IntArray(1 shl (3 * bits)).also { it.fill(-1) } else null
    val sparse = HashMap<Int, Int>()
    val bin = IntArray(total)
    val cnt = mutableListOf<Double>()
    val sr = mutableListOf<Double>()
    val sg = mutableListOf<Double>()
    val sb = mutableListOf<Double>()
    var whites = 0
    for (i in 0 until total) {
        val r = data[i * 4]
        val g = data[i * 4 + 1]
        val b = data[i * 4 + 2]
        if (r > whiteMin && g > whiteMin && b > whiteMin) {
            bin[i] = -1
            whites++
            continue
        }
        val key = ((r shr sh) shl (2 * bits)) or ((g shr sh) shl bits) or (b shr sh)
        var k = if (dense != null) dense[key] else (sparse[key] ?: -1)
        if (k < 0) {
            k = cnt.size
            if (dense != null) dense[key] = k else sparse[key] = k
            cnt.add(0.0)
            sr.add(0.0)
            sg.add(0.0)
            sb.add(0.0)
        }
        bin[i] = k
        cnt[k]++
        sr[k] += r
        sg[k] += g
        sb[k] += b
    }
    return Histogram(
        bin = bin,
        whites = whites,
        cnt = cnt.toDoubleArray(),
        sr = sr.toDoubleArray(),
        sg = sg.toDoubleArray(),
        sb = sb.toDoubleArray(),
    )
}

fun quantize(img: Rgba, colors: Int, whiteMin: Int = 245, mode: PaletteMode = PaletteMode.CLASSIC): Quantized {
    val data = img.data
    val total = img.width * img.height
    var h = histogram(data, total, whiteMin, 5)
    var bits = 6
    while (bits <= 8 && h.cnt.size < colors * BINS_PER_COLOR) {
        h = histogram(data, total, whiteMin, bits)
        bits++
    }
    val bin = h.bin
    val whites = h.whites
    val cnt = h.cnt
    val sr = h.sr
    val sg = h.sg
    val sb = h.sb
    val n = cnt.size
    val keys = IntArray(n) { it }
    val lab = Array(3) { DoubleArray(n) }
    for (k in keys) {
        val c = cnt[k]
        val p = oklabF(sr[k] / c, sg[k] / c, sb[k] / c)
        lab[0][k] = p[0] * c
        lab[1][k] = p[1] * c
        lab[2][k] = p[2] * c
    }
    val palette = mutableListOf<String>()
    val label = IntArray(total)
    val binLabel = IntArray(n).apply { fill(-1) }
    if (keys.isNotEmpty() && mode == PaletteMode.VIVID) {
        val vg = vividLabels(keys, cnt, lab, colors)
        val acc = DoubleArray(vg.k * 5)
        keys.forEachIndexed { i, k ->
            val g = vg.group[i] * 5
            acc[g] += lab[0][k]
            acc[g + 1] += lab[1][k]
            acc[g + 2] += lab[2][k]
            acc[g + 3] += hypot(lab[1][k], lab[2][k])
            acc[g + 4] += cnt[k]
        }
        val remap = IntArray(vg.k) { j ->
            if (acc[j * 5 + 4] != 0.0) {
                palette.add(vividColor(acc[j * 5], acc[j * 5 + 1], acc[j * 5 + 2], acc[j * 5 + 3], acc[j * 5 + 4]))
                palette.size - 1
            } else -1
        }
        keys.forEachIndexed { i, k -> binLabel[k] = remap[vg.group[i]] }
    } else if (keys.isNotEmpty()) {
        var pal = medianCut(keys, cnt, lab, colors)
        repeat(6) {
            val acc = MutableList(pal.size) { DoubleArray(4) }
            for (k in keys) {
                val c = cnt[k]
                val l = lab[0][k] / c
                val a = lab[1][k] / c
                val b = lab[2][k] / c
                var best = 0
                var bd = Double.POSITIVE_INFINITY
                for (p in pal.indices) {
                    val dl = l - pal[p][0]
                    val da = a - pal[p][1]
                    val db = b - pal[p][2]
                    val d = dl * dl + da * da + db * db
                    if (d < bd) { bd = d; best = p }
                }
                binLabel[k] = best
                val arr = acc[best]
                arr[0] += lab[0][k]
                arr[1] += lab[1][k]
                arr[2] += lab[2][k]
                arr[3] += c
            }
            pal = pal.mapIndexed { i, p ->
                if (acc[i][3] != 0.0) doubleArrayOf(acc[i][0] / acc[i][3], acc[i][1] / acc[i][3], acc[i][2] / acc[i][3]) else p
            }
        }

        val rgb = MutableList(pal.size) { DoubleArray(4) }
        for (k in keys) {
            val arr = rgb[binLabel[k]]
            arr[0] += sr[k]
            arr[1] += sg[k]
            arr[2] += sb[k]
            arr[3] += cnt[k]
        }
        val remap = IntArray(rgb.size) { j ->
            if (rgb[j][3] != 0.0) {
                palette.add(toHex(rgb[j][0] / rgb[j][3], rgb[j][1] / rgb[j][3], rgb[j][2] / rgb[j][3]))
                palette.size - 1
            } else -1
        }
        for (k in keys) binLabel[k] = remap[binLabel[k]]
    }
    val white = if (whites > 0) {
        palette.add("#ffffff")
        palette.size - 1
    } else -1
    for (i in 0 until total) label[i] = if (bin[i] < 0) white else binLabel[bin[i]]
    return Quantized(label = label, palette = palette, white = white)
}

fun cohere(img: Rgba, q: Quantized, lambda: Double, mode: PaletteMode = PaletteMode.CLASSIC, rounds: Int = 3): Quantized {
    val n = img.width * img.height
    val k = q.palette.size
    val data = img.data
    val lab = labOf(data, n)
    val label = q.label.copyOf()
    val pal = FloatArray(k * 3)
    var palette = q.palette
    fun setPal() {
        palette.forEachIndexed { i, h ->
            val (r, g, b) = fromHex(h)
            val p = oklabF(r.toDouble(), g.toDouble(), b.toDouble())
            pal[i * 3] = p[0].toFloat()
            pal[i * 3 + 1] = p[1].toFloat()
            pal[i * 3 + 2] = p[2].toFloat()
        }
    }
    setPal()
    repeat(rounds) {
        potts(img.width, img.height, label, lab, pal, lambda)
        if (mode == PaletteMode.VIVID) {
            val acc = DoubleArray(k * 5)
            for (i in 0 until n) {
                val a = label[i] * 5
                val aa = lab[i * 3 + 1]
                val bb = lab[i * 3 + 2]
                acc[a] += lab[i * 3]
                acc[a + 1] += aa
                acc[a + 2] += bb
                acc[a + 3] += hypot(aa.toDouble(), bb.toDouble())
                acc[a + 4]++
            }
            palette = palette.mapIndexed { l, h ->
                if (l == q.white || acc[l * 5 + 4] == 0.0) h
                else vividColor(acc[l * 5], acc[l * 5 + 1], acc[l * 5 + 2], acc[l * 5 + 3], acc[l * 5 + 4])
            }
            setPal()
            return@repeat
        }
        val acc = DoubleArray(k * 4)
        for (i in 0 until n) {
            val a = label[i] * 4
            acc[a] += data[i * 4]
            acc[a + 1] += data[i * 4 + 1]
            acc[a + 2] += data[i * 4 + 2]
            acc[a + 3]++
        }
        palette = palette.mapIndexed { l, h ->
            if (l == q.white || acc[l * 4 + 3] == 0.0) h
            else toHex(acc[l * 4] / acc[l * 4 + 3], acc[l * 4 + 1] / acc[l * 4 + 3], acc[l * 4 + 2] / acc[l * 4 + 3])
        }
        setPal()
    }
    return Quantized(label = label, palette = palette, white = q.white)
}
