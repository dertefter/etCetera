package com.dertefter.design.components.post

import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.SimpleBasePlayer
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

private val VideoAudioAttributes: AudioAttributes = AudioAttributes.Builder()
    .setUsage(C.USAGE_MEDIA)
    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
    .build()

@UnstableApi
@Composable
internal fun rememberVideoPlayer(
    url: String?,
    loop: Boolean = true,
    initiallyMuted: Boolean = true,
): Player {
    if (LocalInspectionMode.current) {
        return remember { PreviewVideoPlayer() }
    }

    val context = LocalContext.current

    val player = remember(context) {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(VideoCache.createMediaSourceFactory(context))
            .setLoadControl(VideoCache.createLoadControl())
            .setAudioAttributes(VideoAudioAttributes, !initiallyMuted)
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                repeatMode = if (loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                volume = if (initiallyMuted) 0f else 1f
            }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                if (events.contains(Player.EVENT_VOLUME_CHANGED)) {
                    player.setAudioAttributes(
                        VideoAudioAttributes,
                        player.volume > 0f
                    )
                }
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
        }
    }

    LaunchedEffect(player, url, loop) {
        player.repeatMode = if (loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        if (url == null) {
            player.clearMediaItems()
            return@LaunchedEffect
        }
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.playWhenReady = true
    }

    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    return player
}

@Composable
internal fun rememberIsBuffering(player: Player): Boolean {
    var isBuffering by remember(player) {
        mutableStateOf(player.playbackState == Player.STATE_BUFFERING)
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
        }
    }

    return isBuffering
}

@Composable
internal fun rememberIsMuted(player: Player): Boolean {
    var isMuted by remember(player) {
        mutableStateOf(player.volume == 0f)
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                if (events.contains(Player.EVENT_VOLUME_CHANGED)) {
                    isMuted = player.volume == 0f
                }
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
        }
    }

    return isMuted
}

@UnstableApi
private class PreviewVideoPlayer : SimpleBasePlayer(Looper.getMainLooper()) {
    override fun getState(): State = State.Builder().setVolume(0f).build()
}