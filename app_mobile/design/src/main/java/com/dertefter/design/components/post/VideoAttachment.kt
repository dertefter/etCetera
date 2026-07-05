package com.dertefter.design.components.post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.ui.compose.PlayerSurface
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@UnstableApi
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideoAttachment(
    attachment: AttachmentUiModel,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val context = LocalContext.current
    val isInspectionMode = LocalInspectionMode.current
    val exoPlayer = remember {
        if (isInspectionMode) null
        else ExoPlayer.Builder(context)
            .setMediaSourceFactory(VideoCache.createMediaSourceFactory(context))
            .setLoadControl(VideoCache.createLoadControl())
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
                playWhenReady = true
                setSeekParameters(SeekParameters.CLOSEST_SYNC)
            }
    }

    var isFirstFrameRendered by remember { mutableStateOf(false) }

    LaunchedEffect(attachment.url) {
        attachment.url?.let {
            isFirstFrameRendered = false
            val mediaItem = MediaItem.fromUri(it)
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
        }
    }

    var isPlaying by remember { mutableStateOf(value = true) }
    var isMuted by remember { mutableStateOf(value = true) }
    var showControls by remember { mutableStateOf(value = false) }
    var isBuffering by remember { mutableStateOf(value = !isInspectionMode) }
    var progress by remember { mutableFloatStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }

    LaunchedEffect(isMuted) {
        exoPlayer?.volume = if (isMuted) 0f else 1f
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
            }

            override fun onRenderedFirstFrame() {
                isFirstFrameRendered = true
            }
        }
        exoPlayer?.addListener(listener)
        onDispose {
            exoPlayer?.removeListener(listener)
            exoPlayer?.release()
        }
    }

    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(3000.milliseconds)
            showControls = false
        }
    }

    LaunchedEffect(isPlaying, isSeeking) {
        if (isInspectionMode || isSeeking) return@LaunchedEffect
        while (true) {
            val currentPos = exoPlayer?.currentPosition?.toFloat() ?: 0f
            val duration = exoPlayer?.duration?.toFloat() ?: 0f
            if (duration > 0) {
                progress = currentPos / duration
            }
            delay(500.milliseconds)
        }
    }

    VideoAttachmentContent(
        exoPlayer = exoPlayer,
        isPlaying = isPlaying,
        isMuted = isMuted,
        showControls = showControls,
        isBuffering = isBuffering,
        isFirstFrameRendered = isFirstFrameRendered,
        progress = progress,
        onToggleControls = { showControls = !showControls },
        onTogglePlayback = {
            exoPlayer?.let {
                isPlaying = if (it.isPlaying) {
                    it.pause()
                    false
                } else {
                    it.play()
                    true
                }
            }
        },
        onToggleMute = { isMuted = !isMuted },
        onSeek = { seekTo ->
            progress = seekTo
            isSeeking = true
        },
        onSeekFinished = {
            exoPlayer?.let {
                val duration = it.duration
                if (duration > 0) {
                    it.seekTo((progress * duration).toLong())
                }
            }
            isSeeking = false
        },
        contentScale = contentScale,
        containerColor = containerColor,
        modifier = modifier
    )
}

@UnstableApi
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideoAttachmentContent(
    exoPlayer: Player?,
    isPlaying: Boolean,
    isMuted: Boolean,
    showControls: Boolean,
    isBuffering: Boolean,
    isFirstFrameRendered: Boolean,
    progress: Float,
    onToggleControls: () -> Unit,
    onTogglePlayback: () -> Unit,
    onToggleMute: () -> Unit,
    onSeek: (Float) -> Unit,
    onSeekFinished: () -> Unit,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val presentationState = rememberPresentationState(exoPlayer)
    Box(
        modifier = modifier
            .background(containerColor)
            .clickable { onToggleControls() }
            .clipToBounds(),
        contentAlignment = Alignment.Center,
    ) {
        if (exoPlayer != null) {
            PlayerSurface(
                player = exoPlayer,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (isFirstFrameRendered) 1f else 0f)
                    .resizeWithContentScale(
                        contentScale = contentScale,
                        sourceSizeDp = presentationState.videoSizeDp
                    ),
            )
        }

        if (isBuffering) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                AppLoadingIndicator(
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

        }

        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center,
            ) {
                FilledTonalIconButton(
                    onClick = onTogglePlayback,
                    modifier = Modifier.size(56.dp),
                    shape = MaterialShapes.Clover8Leaf.toShape(0)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Pause else Icons.Play,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(MaterialTheme.spacing.small),
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .alpha(if (showControls) 1f else 0f)
                .padding(MaterialTheme.spacing.extraLarge+4.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraLarge)
        ) {

            FilledIconButton(
                onClick = onToggleMute,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.VolumeOffFilled else Icons.VolumeUpFilled,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.small),
                )
            }

            Slider(
                value = progress,
                onValueChange = onSeek,
                onValueChangeFinished = onSeekFinished,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondaryFixed,
                    activeTrackColor = MaterialTheme.colorScheme.secondaryFixed,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                )
            )
        }

    }
}

@UnstableApi
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun VideoAttachmentPreview() {
    AppTheme {
        VideoAttachmentContent(
            exoPlayer = null,
            isPlaying = true,
            showControls = true,
            isBuffering = false,
            isFirstFrameRendered = true,
            progress = 0.5f,
            onToggleControls = {},
            onTogglePlayback = {},
            onToggleMute = {},
            onSeek = {},
            onSeekFinished = {},
            isMuted = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )
    }
}
