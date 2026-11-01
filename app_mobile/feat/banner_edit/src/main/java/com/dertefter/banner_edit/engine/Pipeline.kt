package com.dertefter.banner_edit.engine


import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.pow


enum class EngineKind { LAYERS, EXACT, STROKES }


data class Detail(
    val cell: Int,
    val colors: Int,
    val smooth: Double,
    val minArea: Int,
)

data class Params(
    val engine: EngineKind = EngineKind.LAYERS,
    val auto: Boolean = true,
    val paletteMode: PaletteMode = PaletteMode.VIVID,
    val merge: Int = 0,
    val sharpEdges: Boolean = false,
    val cell: Int = 1,
    val colors: Int = 48,
    val smooth: Double = 0.0,
    val minArea: Int = 4,
    val tol: Double = 0.4,
    val routePoints: Int = 24,
    val whiteMin: Int = 245,
    val adjust: Adjust = NO_ADJUST,
)


fun mergeLambda(v: Int): Double = if (v > 0) 1e-4 * 300.0.pow(v / 100.0) else 0.0

val LADDER: List<Detail> = listOf(
    Detail(1, 256, 0.0, 1),
    Detail(1, 128, 0.0, 1),
    Detail(1, 128, 0.0, 2),
    Detail(1, 128, 0.0, 4),
    Detail(1, 128, 0.0, 8),
    Detail(1, 128, 1.0, 8),
    Detail(1, 128, 1.0, 16),
    Detail(1, 128, 1.0, 24),
    Detail(1, 128, 1.0, 48),
    Detail(1, 128, 2.0, 64),
    Detail(1, 96, 2.0, 96),
    Detail(1, 64, 2.0, 128),
    Detail(1, 48, 2.0, 192),
    Detail(2, 48, 1.0, 256),
    Detail(2, 32, 1.0, 384),
    Detail(2, 24, 2.0, 512),
    Detail(3, 24, 2.0, 768),
    Detail(3, 16, 2.0, 1024),
    Detail(4, 16, 2.0, 1536),
    Detail(4, 12, 2.0, 2048),
    Detail(6, 10, 2.0, 3072),
    Detail(8, 8, 2.0, 4096),
)

class Tried(val detail: Detail, val ops: Int, val points: Int, val fits: Boolean)

class PipelineResult(
    val ops: List<Op>,
    val stats: Stats,
    val quality: Quality,
    val detail: Detail,
    val palette: List<String>,
    val tried: List<Tried>,
)

class BuildResult(val ops: List<Op>, val palette: List<String>)

class Session(val src: Rgba) {
    private var adjKey = ""
    private var target: Rgba = src
    private var targetLab: FloatArray = labOf(src.data, src.width * src.height)
    private val grids = HashMap<Int, Rgba>()
    private val quants = HashMap<String, Quantized>()

    private fun adjustKey(p: Params): String =
        "${p.adjust.brightness}/${p.adjust.contrast}/${p.adjust.saturation}/${p.adjust.denoise}"

    private fun prepare(p: Params) {
        val key = adjustKey(p)
        if (key == adjKey) return
        adjKey = key
        target = adjust(src, p.adjust)
        targetLab = labOf(target.data, target.width * target.height)
        grids.clear()
        quants.clear()
    }

    private fun grid(cell: Int): Rgba = grids.getOrPut(cell) { downsample(target, cell) }

    private fun quant(cell: Int, colors: Int, whiteMin: Int, mode: PaletteMode, merge: Int): Quantized {
        val key = "$cell/$colors/$whiteMin/$mode/$merge"
        quants[key]?.let { return it }
        val q = if (merge > 0) {
            cohere(grid(cell), quant(cell, colors, whiteMin, mode, 0), mergeLambda(merge) / cell, mode)
        } else {
            quantize(grid(cell), colors, whiteMin, mode)
        }
        quants[key] = q
        return q
    }
    fun build(p: Params, d: Detail, limit: Boolean, onProgress: ((Double) -> Unit)? = null): BuildResult? {
        prepare(p)
        val g = grid(d.cell)
        val q = quant(d.cell, d.colors, p.whiteMin, p.paletteMode, p.merge)
        val K = q.palette.size
        var label = q.label
        if (K > 1) {
            val lab = FloatArray(K * 3)
            q.palette.forEachIndexed { k, h ->
                val l = oklabFFromHex(h)
                lab[k * 3] = l[0].toFloat()
                lab[k * 3 + 1] = l[1].toFloat()
                lab[k * 3 + 2] = l[2].toFloat()
            }
            label = label.copyOf()
            if (p.sharpEdges) absorbEdges(g.width, g.height, label, K, lab)
            if (d.smooth > 0) label = despeckle(g.width, g.height, label, K)
            val full = floor(d.smooth + 1e-9).toInt()
            val part = d.smooth - full
            for (i in 0 until full) label = smoothLabels(g.width, g.height, label, K)
            if (part > 0.01) label = smoothLabels(g.width, g.height, label, K, 9 - part.roundToIntCompat4())
            mergeSmall(g.width, g.height, label, K, max(1, (d.minArea.toDouble() / (d.cell * d.cell)).roundToIntCompat4()), lab)
        }
        val abortOps = if (limit) LIMITS.OPS else null
        val abortPoints = if (limit) LIMITS.POINTS else null
        return try {
            val ops = if (p.engine == EngineKind.EXACT) {
                exact(g.width, g.height, label, q.palette, q.white, ExactOptions(d.cell, abortOps, abortPoints, onProgress))
            } else {
                layers(
                    g.width, g.height, label, q.palette, q.white,
                    LayersOptions(d.cell, p.tol, p.routePoints, abortOps, abortPoints, onProgress),
                )
            }
            BuildResult(splitLong(ops), q.palette)
        } catch (_: Aborted) {
            null
        }
    }

    fun quality(ops: List<Op>): Quality {
        val w = target.width
        val h = target.height
        return compare(renderOps(ops, w, h), targetLab, w * h)
    }

    private fun fits(ops: List<Op>): Boolean {
        val s = stats(ops)
        return s.ops <= LIMITS.OPS && s.points <= LIMITS.POINTS
    }

    fun run(p: Params, onProgress: OnProgress = { _, _ -> }): PipelineResult {
        prepare(p)
        val tried = mutableListOf<Tried>()
        class Best(val ops: List<Op>, val palette: List<String>, val detail: Detail)

        val b: Best = when (p.engine) {
            EngineKind.STROKES -> {
                val d = Detail(1, 0, 0.0, 1)
                val ops = strokes(target, StrokesOptions(LIMITS.OPS) { f -> onProgress("Мазки", f) })
                val s = stats(ops)
                tried.add(Tried(d, s.ops, s.points, s.ops <= LIMITS.OPS && s.points <= LIMITS.POINTS))
                Best(ops, emptyList(), d)
            }

            else -> if (!p.auto) {
                val d = Detail(p.cell, p.colors, p.smooth, p.minArea)
                val r = build(p, d, false) { f -> onProgress("Штрихи", f) }!!
                val s = stats(r.ops)
                tried.add(Tried(d, s.ops, s.points, fits(r.ops)))
                Best(r.ops, r.palette, d)
            } else {
                var lo = 0
                var hi = LADDER.size - 1
                var found: Best? = null
                val total = ceil(ln(LADDER.size.toDouble()) / ln(2.0)).toInt() + 1
                val step = intArrayOf(0) // шаг в массиве, чтобы менять из лямбды прогресса
                while (lo <= hi) {
                    val mid = (lo + hi) shr 1
                    val d = LADDER[mid]
                    val r = build(p, d, true) { f -> onProgress("Подбор ${step[0] + 1}/$total", (step[0] + f) / total) }
                    step[0]++
                    val s = r?.ops?.let { stats(it) }
                    val ok = r != null && s != null && s.ops <= LIMITS.OPS && s.points <= LIMITS.POINTS
                    tried.add(Tried(d,
                        s?.ops ?: (LIMITS.OPS + 1), s?.points ?: (LIMITS.POINTS + 1), ok))
                    if (ok) {
                        found = Best(r.ops, r.palette, d)
                        hi = mid - 1
                    } else {
                        lo = mid + 1
                    }
                }
                found ?: run {
                    val d = LADDER[LADDER.size - 1]
                    val r = build(p, d, false)!!
                    Best(r.ops, r.palette, d)
                }
            }
        }
        onProgress("Перекраска", 1.0)
        val ops = recolor(b.ops, targetLab, target.width, target.height)
        onProgress("Оценка", 1.0)
        return PipelineResult(ops, stats(ops), quality(ops), b.detail, b.palette, tried)
    }
}

private fun oklabFFromHex(h: String): DoubleArray {
    val rgb = fromHex(h)
    return oklab(rgb[0], rgb[1], rgb[2])
}

private fun Double.roundToIntCompat4(): Int = floor(this + 0.5).toInt()
