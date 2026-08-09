package com.dertefter.design.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dertefter.design.R
import com.dertefter.design.icons.Icons
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.blur.materials.HazeMaterials


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

    val hazeStyle = HazeMaterials.ultraThin(
        containerColor = containerColor
    )

    Box(
        modifier = modifier
            .padding(4.dp)
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ){

        Box(
            modifier = Modifier
                .then(
                    if (hazeState != null) {
                        Modifier.hazeBlur(
                            input = HazeInput.Sources(hazeState),
                            style = hazeStyle
                        )
                    } else {
                        Modifier
                            .background(containerColor)
                    }
                )
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