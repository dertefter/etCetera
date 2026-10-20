package com.dertefter.settings_about.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AppAboutHeader(
    modifier: Modifier = Modifier,
    appName: String,
    desc: String
) {

    val image = AnimatedImageVector.animatedVectorResource(com.dertefter.design.R.drawable.app_icon)
    var atEnd by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300.milliseconds)
        while (true) {
            atEnd = !atEnd
            delay(7000.milliseconds)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraLarge)
    ) {

        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.largeIncreased)
                .size(74.dp)
        ){
            Icon(
                painter = rememberAnimatedVectorPainter(image, atEnd),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .fillMaxSize()
            )
        }



        Column {
            Text(
                text = appName,
                style = MaterialTheme.typography.headlineSmallEmphasized
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.labelLargeEmphasized
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun AppAboutHeaderPreview() {
    AppTheme(
        content = {
            AppAboutHeader(
                appName = "etCetera",
                desc = "v 1.0.0"
            )
        },
    )
}
