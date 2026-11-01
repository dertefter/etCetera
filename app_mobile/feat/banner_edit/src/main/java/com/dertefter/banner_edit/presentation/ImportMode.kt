package com.dertefter.banner_edit.presentation

import com.dertefter.banner_edit.R
import com.dertefter.banner_edit.engine.EngineKind

enum class ImportMode(val labelRes: Int) {
    STROKES(R.string.banner_edit_mode_strokes),
    LAYERS(R.string.banner_edit_mode_layers),

    EXACT(R.string.banner_edit_mode_exact),
    ;

    val engine: EngineKind
        get() = when (this) {
            STROKES -> EngineKind.STROKES
            LAYERS -> EngineKind.LAYERS
            EXACT -> EngineKind.EXACT
        }
}
