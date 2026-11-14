package com.dertefter.design.components.post

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.ContentFrame
import androidx.media3.ui.compose.material3.buttons.PlayPauseButton
import androidx.media3.ui.compose.material3.indicator.ProgressSlider
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@OptIn(UnstableApi::class)
@Composable
fun VideoAttachment(
    modifier: Modifier = Modifier,
    attachment: AttachmentUiModel,
    contentPadding: PaddingValues = PaddingValues(),
    onClick: () -> Unit = {},
    isFullscreen: Boolean = false,
    contentScale: AttachmentContentScale? = null,
    containerColor: Color? = null,
) {

    val videoContentScale = when (contentScale) {
        AttachmentContentScale.FIT -> ContentScale.Fit
        AttachmentContentScale.CROP -> ContentScale.Crop
        null -> if (isFullscreen) ContentScale.Fit else ContentScale.Crop
    }

    val backgroundColor =
        containerColor ?: if (isFullscreen) Color.Black else MaterialTheme.colorScheme.surfaceVariant

    val controlColors = IconButtonDefaults.filledIconButtonColors(
        containerColor = Color.Black.copy(alpha = 0.5f),
        contentColor = Color.White,
    )

    val player = rememberVideoPlayer(
        url = attachment.url,
        loop = true,
        initiallyMuted = true,
    )

    val isBuffering = rememberIsBuffering(player)

    val isMuted = rememberIsMuted(player)

    var visibleControls by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                visibleControls = !visibleControls
            }
    ) {
        ContentFrame(
            player = player,
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxWidth(),
            contentScale = videoContentScale,
            shutter = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                )
            }
        )

        if (isBuffering) {
            AppLoadingIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (!isFullscreen) {
            AppNavigationIcon(
                onClick = { player.volume = if (isMuted) 1f else 0f },
                modifier = Modifier
                    .padding(MaterialTheme.spacing.small)
                    .size(44.dp)
                    .align(Alignment.TopStart),
                containerColor = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White,
                icon = if (isMuted) Icons.VolumeOffFilled else Icons.VolumeUpFilled,
                contentDescription = null
            )

            AppNavigationIcon(
                onClick = onClick,
                modifier = Modifier
                    .padding(MaterialTheme.spacing.small)
                    .size(44.dp)
                    .align(Alignment.TopEnd),
                containerColor = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White,
                icon = Icons.Fullscreen,
                contentDescription = null
            )
        }

        AnimatedVisibility(
            visible = visibleControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            PlayPauseButton(
                player = player,
                colors = controlColors,
                modifier = Modifier.size(56.dp),
            )
        }

        AnimatedVisibility(
            visible = visibleControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(contentPadding)
                .padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.medium)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                if (isFullscreen) {
                    AppNavigationIcon(
                        onClick = { player.volume = if (isMuted) 1f else 0f },
                        modifier = Modifier
                            .padding(bottom = MaterialTheme.spacing.small)
                            .size(44.dp),
                        containerColor = Color.Black.copy(alpha = 0.5f),
                        contentColor = Color.White,
                        icon = if (isMuted) Icons.VolumeOffFilled else Icons.VolumeUpFilled,
                        contentDescription = null
                    )
                }
                ProgressSlider(
                    player = player,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

}

@Preview(showBackground = false)
@Composable
fun VideoAttachmentPreview() {
    AppTheme(
        content = {
            VideoAttachment(
                attachment = AttachmentUiModel(
                    id = "1",
                    type = "video",
                    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    mimeType = "video/mp4"
                ),
                modifier = Modifier.size(200.dp),
            )
        },
    )
}