package com.dertefter.design.components.loading

import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.theme.AppTheme
import org.intellij.lang.annotations.Language

@Language("AGSL")
const val PERLIN_SHIMMER_AGSL = """
    uniform float2 iResolution;
    uniform float iTime;
    uniform vec4 baseColor;
    uniform vec4 highlightColor;
    uniform float scale;

    // 3D Hash function
    vec3 hash(vec3 p) {
        p = vec3(dot(p, vec3(127.1, 311.7, 74.7)),
                 dot(p, vec3(269.5, 183.3, 246.1)),
                 dot(p, vec3(113.5, 271.9, 124.6)));
        return -1.0 + 2.0 * fract(sin(p) * 43758.5453123);
    }

    // 3D Perlin Noise
    float perlin_noise(vec3 p) {
        vec3 i = floor(p);
        vec3 f = fract(p);
        vec3 u = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);

        return mix(
            mix(mix(dot(hash(i + vec3(0.0, 0.0, 0.0)), f - vec3(0.0, 0.0, 0.0)), 
                    dot(hash(i + vec3(1.0, 0.0, 0.0)), f - vec3(1.0, 0.0, 0.0)), u.x),
                mix(dot(hash(i + vec3(0.0, 1.0, 0.0)), f - vec3(0.0, 1.0, 0.0)), 
                    dot(hash(i + vec3(1.0, 1.0, 0.0)), f - vec3(1.0, 1.0, 0.0)), u.x), u.y),
            mix(mix(dot(hash(i + vec3(0.0, 0.0, 1.0)), f - vec3(0.0, 0.0, 1.0)), 
                    dot(hash(i + vec3(1.0, 0.0, 1.0)), f - vec3(1.0, 0.0, 1.0)), u.x),
                mix(dot(hash(i + vec3(0.0, 1.0, 1.0)), f - vec3(0.0, 1.0, 1.0)), 
                    dot(hash(i + vec3(1.0, 1.0, 1.0)), f - vec3(1.0, 1.0, 1.0)), u.x), u.y), 
            u.z);
    }

    half4 main(in float2 fragCoord) {
        vec2 uv = fragCoord / iResolution.xy;
        uv.x *= iResolution.x / iResolution.y;

        // --- PARAMETERS ---
        float speed = 0.4;
        // ------------------

        // 3D Coordinate: (X, Y, Time as Z)
        vec3 p = vec3(uv * scale, iTime * speed);

        // Get raw noise in [-0.5, 0.5] range and map to [0, 1] with contrast
        float raw_n = perlin_noise(p);
        float n = smoothstep(-0.35, 0.35, raw_n);

        vec4 col;
        if (baseColor.a >= 0.99 && highlightColor.a < 1.0) {
            // Opaque base color with semi-transparent highlight overlay
            vec3 targetRgb = mix(baseColor.rgb, highlightColor.rgb, highlightColor.a);
            col = vec4(mix(baseColor.rgb, targetRgb, n), 1.0);
        } else {
            // General RGBA interpolation
            col = mix(baseColor, highlightColor, n);
        }

        // Return premultiplied alpha for Skia / AGSL
        return half4(col.rgb * col.a, col.a);
    }
"""

@Composable
fun Modifier.shimmer(
    baseColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    highlightColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    scale: Float = 2f,
    speed: Float = 1f,
    shape: Shape? = null
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "ShimmerTransition")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 100_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTime"
    )
    val animatedTime = time * speed

    val isAgslSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    val runtimeShader = remember(isAgslSupported) {
        if (isAgslSupported) {
            RuntimeShader(PERLIN_SHIMMER_AGSL)
        } else {
            null
        }
    }

    val clippedModifier = if (shape != null) this.clip(shape) else this

    return clippedModifier.drawWithCache {
        val brush = if (isAgslSupported && runtimeShader != null) {
            runtimeShader.setFloatUniform("iResolution", size.width, size.height)
            runtimeShader.setFloatUniform("iTime", animatedTime)
            runtimeShader.setFloatUniform(
                "baseColor",
                baseColor.red,
                baseColor.green,
                baseColor.blue,
                baseColor.alpha
            )
            runtimeShader.setFloatUniform(
                "highlightColor",
                highlightColor.red,
                highlightColor.green,
                highlightColor.blue,
                highlightColor.alpha
            )
            runtimeShader.setFloatUniform("scale", scale)
            ShaderBrush(runtimeShader)
        } else {
            val offset = (animatedTime * 500f) % (size.width + size.height + 1f)
            Brush.linearGradient(
                colors = listOf(baseColor, highlightColor, baseColor),
                start = Offset(offset - size.width, offset - size.height),
                end = Offset(offset, offset)
            )
        }

        onDrawBehind {
            drawRect(brush = brush)
        }
    }
}

@Composable
fun Shimmer(
    modifier: Modifier = Modifier,
    baseColor: Color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
    highlightColor: Color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
    scale: Float = 2f,
    speed: Float = 0.08f,
) {
    Box(
        modifier = modifier.shimmer(
            baseColor = baseColor,
            highlightColor = highlightColor,
            scale = scale,
            speed = speed
        )
    )
}

@Preview
@Composable
fun ShimmerPreview() {
    AppTheme {
        Shimmer(
            modifier = Modifier.size(
                width = 300.dp,
                height = 150.dp
            ).background(Color.Red)
        )
    }
}
