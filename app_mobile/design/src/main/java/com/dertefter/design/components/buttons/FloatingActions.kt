package com.dertefter.design.components.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing


data class FloatingAction(
    val icon: ImageVector,
    val contentDescription: String? = null,
    val onClick: () -> Unit = {}
)

enum class FloatingActionsScrolledStatus {
    IDLE, SCROLLED, MORE_SCROLLED
}

@Composable
fun FloatingActions(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    floatingActionsScrolledStatus: FloatingActionsScrolledStatus = FloatingActionsScrolledStatus.IDLE,
    primaryFloatingAction: FloatingAction? = null,
    secondaryFloatingAction: FloatingAction? = null
) {

    val largeRadius = MaterialTheme.rounding.extraLargeIncreased
    val mediumRadius = MaterialTheme.rounding.largeIncreased
    val smallRadius = MaterialTheme.rounding.medium

    val largeContainerSize = 86.dp
    val mediumContainerSize = 68.dp
    val smallContainerSize = 40.dp

    val largeIconSize = 34.dp
    val mediumIconSize = 28.dp
    val smallIconSize = 24.dp


    val primaryFloatingActionContainerSize by animateDpAsState(
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        targetValue = when (
            floatingActionsScrolledStatus
        ) {
            FloatingActionsScrolledStatus.IDLE -> largeContainerSize
            FloatingActionsScrolledStatus.SCROLLED -> mediumContainerSize
            FloatingActionsScrolledStatus.MORE_SCROLLED -> smallContainerSize
        }
    )

    val primaryFloatingActionIconSize by animateDpAsState(
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        targetValue = when (
            floatingActionsScrolledStatus
        ) {
            FloatingActionsScrolledStatus.IDLE -> largeIconSize
            FloatingActionsScrolledStatus.SCROLLED -> mediumIconSize
            FloatingActionsScrolledStatus.MORE_SCROLLED -> smallIconSize
        }
    )

    val primaryFloatingActionRadius by animateDpAsState(
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        targetValue = when (
            floatingActionsScrolledStatus
        ) {
            FloatingActionsScrolledStatus.IDLE -> largeRadius
            FloatingActionsScrolledStatus.SCROLLED -> mediumRadius
            FloatingActionsScrolledStatus.MORE_SCROLLED -> smallRadius
        }
    )

    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
    )
    {
        primaryFloatingAction?.let { primaryFloatingAction ->
            SmallFloatingActionButton(
                modifier = Modifier.size(primaryFloatingActionContainerSize),
                shape = RoundedCornerShape(primaryFloatingActionRadius),
                onClick = primaryFloatingAction.onClick,
            ) {
                Icon(
                    modifier = Modifier.size(primaryFloatingActionIconSize),
                    imageVector = primaryFloatingAction.icon,
                    contentDescription = primaryFloatingAction.contentDescription
                )
            }
        }
        secondaryFloatingAction?.let { secondaryFloatingAction ->
            AnimatedVisibility(
                visible = floatingActionsScrolledStatus == FloatingActionsScrolledStatus.MORE_SCROLLED,
                enter = fadeIn(
                    animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                ) + scaleIn(
                    animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                ) + expandVertically(
                    animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                ),
                exit = fadeOut(
                    animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                ) + scaleOut(
                    animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                ) + shrinkVertically(
                    animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                )
            ) {
                SmallFloatingActionButton(
                    modifier = Modifier
                        .padding(top = MaterialTheme.spacing.large)
                        .size(mediumContainerSize),
                    shape = RoundedCornerShape(mediumRadius),
                    onClick = secondaryFloatingAction.onClick,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = secondaryFloatingAction.icon,
                        modifier = Modifier.size(mediumIconSize),
                        contentDescription = secondaryFloatingAction.contentDescription
                    )
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun FloatingActionsIdlePreview() {
    AppTheme(
        content = {
            FloatingActions(
                floatingActionsScrolledStatus = FloatingActionsScrolledStatus.IDLE,
                primaryFloatingAction = FloatingAction(
                    icon = Icons.Add,
                    contentDescription = "Create post"
                )
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
fun FloatingActionsScrolledPreview() {
    AppTheme(
        content = {
            FloatingActions(
                floatingActionsScrolledStatus = FloatingActionsScrolledStatus.SCROLLED,
                primaryFloatingAction = FloatingAction(
                    icon = Icons.Add,
                    contentDescription = "Create post"
                )
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
fun FloatingActionsMoreScrolledPreview() {
    AppTheme(
        content = {
            FloatingActions(
                floatingActionsScrolledStatus = FloatingActionsScrolledStatus.MORE_SCROLLED,
                primaryFloatingAction = FloatingAction(
                    icon = Icons.Add,
                    contentDescription = "Create post"
                ),
                secondaryFloatingAction = FloatingAction(
                    icon = Icons.ArrowWarmUp,
                    contentDescription = "Scroll to top"
                )
            )
        },
    )
}