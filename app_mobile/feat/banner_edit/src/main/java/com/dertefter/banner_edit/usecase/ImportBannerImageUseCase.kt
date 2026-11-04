package com.dertefter.banner_edit.usecase

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import com.dertefter.banner_edit.engine.Adjust
import com.dertefter.banner_edit.engine.EngineKind
import com.dertefter.banner_edit.engine.LIMITS
import com.dertefter.banner_edit.engine.NO_ADJUST
import com.dertefter.banner_edit.engine.Op
import com.dertefter.banner_edit.engine.Params
import com.dertefter.banner_edit.engine.Rgba
import com.dertefter.banner_edit.engine.Session
import com.dertefter.banner_edit.engine.splitLong
import com.dertefter.banner_edit.presentation.CropRect
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import androidx.compose.ui.geometry.Offset
import com.dertefter.banner_edit.presentation.drawbox.BannerStroke
import com.dertefter.banner_edit.engine.TOOL_BRUSH
import kotlin.math.max
import androidx.core.graphics.createBitmap


class ImportResult(val strokes: List<BannerStroke>)

@Inject
class ImportBannerImageUseCase(
    private val application: Application,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    suspend operator fun invoke(
        uri: Uri,
        engine: EngineKind = EngineKind.STROKES,
        adjust: Adjust = NO_ADJUST,
        cropRect: CropRect? = null,
        onProgress: (stage: String, done: Double) -> Unit,
    ): ImportResult = withContext(dispatcher) {
        val src = decodeCover(uri, cropRect)
        val params = Params(engine = engine, adjust = adjust)
        val result = Session(src).run(params) { stage, done ->
            ensureActive()
            onProgress(stage, done)
        }
        ImportResult(toStrokes(splitLong(result.ops)))
    }

    private fun decodeCover(uri: Uri, cropRect: CropRect?): Rgba {
        val resolver = application.contentResolver
        val opts = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri).use { android.graphics.BitmapFactory.decodeStream(it, null, opts) }
        if (opts.outWidth <= 0 || opts.outHeight <= 0) throw IllegalArgumentException("не картинка")
        var sample = 1
        while (opts.outWidth.toLong() * opts.outHeight.toLong() / (sample.toLong() * sample.toLong()) > MAX_DECODE_PIXELS) sample *= 2
        val real = android.graphics.BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val decoded = resolver.openInputStream(uri).use { android.graphics.BitmapFactory.decodeStream(it, null, real) }
            ?: throw IllegalArgumentException("не удалось прочитать картинку")
        try {
            val cropped: Bitmap = if (cropRect != null) {
                val scaleX = decoded.width.toFloat() / opts.outWidth
                val scaleY = decoded.height.toFloat() / opts.outHeight
                val l = (cropRect.left * scaleX).toInt().coerceIn(0, decoded.width - 1)
                val t = (cropRect.top * scaleY).toInt().coerceIn(0, decoded.height - 1)
                val r = (cropRect.right * scaleX).toInt().coerceIn(l + 1, decoded.width)
                val b = (cropRect.bottom * scaleY).toInt().coerceIn(t + 1, decoded.height)
                Bitmap.createBitmap(decoded, l, t, r - l, b - t)
            } else {
                decoded
            }
            return coverCropToRgba(cropped, recycleIfNeeded = cropped !== decoded)
        } finally {
            if (!decoded.isRecycled) decoded.recycle()
        }
    }

    private fun coverCropToRgba(src: Bitmap, recycleIfNeeded: Boolean = false): Rgba {
        val scale = max(LIMITS.W.toDouble() / src.width, LIMITS.H.toDouble() / src.height)
        val out = createBitmap(LIMITS.W, LIMITS.H)
        val canvas = android.graphics.Canvas(out)
        canvas.drawColor(android.graphics.Color.WHITE)
        val m = Matrix()
        m.setScale(scale.toFloat(), scale.toFloat())
        m.postTranslate(((LIMITS.W - src.width * scale) / 2).toFloat(), ((LIMITS.H - src.height * scale) / 2).toFloat())
        val paint = android.graphics.Paint().apply {
            isFilterBitmap = true
            isAntiAlias = true
        }
        canvas.drawBitmap(src, m, paint)
        if (recycleIfNeeded && !src.isRecycled) src.recycle()
        val pixels = IntArray(LIMITS.W * LIMITS.H)
        out.getPixels(pixels, 0, LIMITS.W, 0, 0, LIMITS.W, LIMITS.H)
        out.recycle()

        val rgba = IntArray(LIMITS.W * LIMITS.H * 4)
        for (i in pixels.indices) {
            val p = pixels[i]
            rgba[i * 4] = (p shr 16) and 0xFF
            rgba[i * 4 + 1] = (p shr 8) and 0xFF
            rgba[i * 4 + 2] = p and 0xFF
            rgba[i * 4 + 3] = (p shr 24) and 0xFF
        }
        return Rgba(LIMITS.W, LIMITS.H, rgba)
    }

    private fun toStrokes(ops: List<Op>): List<BannerStroke> =
        ops.filter { it.tool == TOOL_BRUSH && it.points.isNotEmpty() }.map { o ->
            BannerStroke(
                colorHex = o.color,
                width = o.width.toFloat(),
                points = o.points.map { Offset(it.x.toFloat(), it.y.toFloat()) },
            )
        }

    private companion object {
        const val MAX_DECODE_PIXELS = 16_000_000L
    }
}
