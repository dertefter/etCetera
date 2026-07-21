package com.dertefter.design.components.post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.spacing
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import io.github.kdroidfilter.composemediaplayer.CacheConfig
import io.github.kdroidfilter.composemediaplayer.VideoPlayerSurface
import io.github.kdroidfilter.composemediaplayer.rememberVideoPlayerState

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideoAttachment(
    modifier: Modifier = Modifier,
    attachment: AttachmentUiModel,
    contentPadding: PaddingValues = PaddingValues(),
    onClick: () -> Unit = {},
    isFullscreen: Boolean = false,
) {

    val hazeState = rememberHazeState()

    val playerState = rememberVideoPlayerState(
        cacheConfig = CacheConfig(
            enabled = true,
            maxCacheSizeBytes = 200L * 1024L * 1024L
        )
    )

    LaunchedEffect(attachment.url) {
        attachment.url?.let {
            playerState.openUri(it)
        }
    }

    var mute by remember { mutableStateOf(true) }

    var visibleControls by remember { mutableStateOf(false) }

    playerState.volume = if (mute) 0f else 1f
    playerState.loop = true

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
        VideoPlayerSurface(
            playerState = playerState,
            contentScale = if (isFullscreen) ContentScale.Fit else ContentScale.Crop,
            modifier = Modifier
                .hazeSource(state = hazeState)
                .background(if (isFullscreen) Color.Black else MaterialTheme.colorScheme.surfaceVariant)
                .fillMaxWidth()
        )

        if (playerState.isLoading) {
            AppLoadingIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (!isFullscreen) {
            AppNavigationIcon(
                onClick = {
                    mute = !mute
                },
                modifier = Modifier
                    .padding(MaterialTheme.spacing.small)
                    .size(44.dp)
                    .align(Alignment.TopStart),
                containerColor = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White,
                hazeState = hazeState,
                icon = if (!mute) {
                    Icons.VolumeUpFilled
                } else {
                    Icons.VolumeOffFilled
                },
                contentDescription = null
            )
        }
        if (!isFullscreen){
            AppNavigationIcon(
                onClick = onClick,
                modifier = Modifier
                    .padding(MaterialTheme.spacing.small)
                    .size(44.dp)
                    .align(Alignment.TopEnd),
                containerColor = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White,
                hazeState = hazeState,
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
            AppNavigationIcon(
                onClick = {
                    if (playerState.isPlaying) playerState.pause() else playerState.play()
                },
                containerColor = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White,
                hazeState = hazeState,
                modifier = Modifier.size(56.dp),
                icon =  if (playerState.isPlaying) Icons.Pause else Icons.Play
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
                        onClick = {
                            mute = !mute
                        },
                        modifier = Modifier
                            .padding(bottom = MaterialTheme.spacing.small)
                            .size(44.dp),
                        containerColor = Color.Black.copy(alpha = 0.5f),
                        contentColor = Color.White,
                        hazeState = hazeState,
                        icon = if (!mute) {
                            Icons.VolumeUpFilled
                        } else {
                            Icons.VolumeOffFilled
                        },
                        contentDescription = null
                    )
                }
                Slider(
                    value = playerState.sliderPos,
                    onValueChange = { playerState.seekStart(it) },
                    onValueChangeFinished = { playerState.seekFinished() },
                    valueRange = 0f..1000f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

}
