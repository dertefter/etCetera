package com.dertefter.design.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.luminance
import com.dertefter.design.R
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.circleShape
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurDefaults
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect


@Composable
fun AppNavigationIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector = Icons.ArrowBack,
    contentDescription: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    hazeState: HazeState? = null
){

    val ultraThin = HazeBlurDefaults.style(
        backgroundColor = containerColor,
        blurRadius = 24.dp,
        noiseFactor = 0.15f,
    )

    Box(
        modifier = modifier
            .padding(4.dp)
            .size(40.dp)
            .clip(MaterialTheme.circleShape())
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ){

        Box(
            modifier = Modifier
                .hazeEffect(state = hazeState) {
                    blurEffect {
                        style = ultraThin
                    }
                }
                .fillMaxSize()
        )

        Icon(
            imageVector = icon,
            tint = contentColor,
            contentDescription = contentDescription ?: stringResource(R.string.design_back_content_desc),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
private fun AppNavigationIconPreview(){
    AppTheme {
        AppNavigationIcon(
            onClick = {}
        )
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
private fun AppNavigationIconPreview2(){
    AppTheme(
        isCut = true
    ) {
        AppNavigationIcon(
            onClick = {}
        )
    }
}
