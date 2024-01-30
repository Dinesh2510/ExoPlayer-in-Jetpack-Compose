package com.player.demo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_WHEN_PLAYING


val video_Url: String =
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
val video_Url_HLS: String = "https://assets.afcdn.com/video49/20210722/v_645516.m3u8"

//https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8
var passedString: String = ""

class VideoPlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    passedString = intent.getStringExtra("Key").toString()
                    Player()
                }
            }
        }
    }
}

@Composable
@androidx.annotation.OptIn(UnstableApi::class)
fun Player() {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    var player: Player? by remember {
        mutableStateOf(null)
    }


    val enterFullscreen = { activity.requestedOrientation = SCREEN_ORIENTATION_USER_LANDSCAPE }
    val exitFullscreen = {
        // Will reset to SCREEN_ORIENTATION_USER later
        activity.requestedOrientation = SCREEN_ORIENTATION_USER
    }


    val playerView = createPlayerView(player)
    activity.requestedOrientation = SCREEN_ORIENTATION_USER
    playerView.controllerAutoShow = true
    playerView.keepScreenOn = true
    playerView.setShowBuffering(SHOW_BUFFERING_WHEN_PLAYING)
    playerView.setFullscreenButtonClickListener { isFullScreen ->
        with(context) {
            if (isFullScreen) {
                if (activity.requestedOrientation == SCREEN_ORIENTATION_USER) {
                    enterFullscreen()
                }
            } else {
                exitFullscreen()
            }
        }
    }
    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                if (Build.VERSION.SDK_INT > 23) {
                    player = initPlayer(context)
                    playerView.onResume()
                }
            }

            Lifecycle.Event.ON_RESUME -> {
                if (Build.VERSION.SDK_INT <= 23) {
                    player = initPlayer(context)
                    playerView.onResume()
                }
            }

            Lifecycle.Event.ON_PAUSE -> {
                if (Build.VERSION.SDK_INT <= 23) {
                    playerView.apply {
                        player?.release()
                        onPause()
                        player = null
                    }
                }
            }

            Lifecycle.Event.ON_STOP -> {
                if (Build.VERSION.SDK_INT > 23) {
                    playerView.apply {
                        player?.release()
                        onPause()
                        player = null
                    }
                }
            }

            else -> {}
        }
    }

    AndroidView(
        factory = { playerView }
    )
}
@Composable
fun ComposableLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit,
) {
    DisposableEffect(lifecycleOwner) {
        // 1. Create a LifecycleEventObserver to handle lifecycle events.
        val observer = LifecycleEventObserver { source, event ->
            // 2. Call the provided onEvent callback with the source and event.
            onEvent(source, event)
        }

        // 3. Add the observer to the lifecycle of the provided LifecycleOwner.
        lifecycleOwner.lifecycle.addObserver(observer)

        // 4. Remove the observer when the composable is disposed.
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}


@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun initPlayer(context: Context): Player {
    return ExoPlayer.Builder(context).build().apply {
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
        val uri =
            Uri.parse(passedString)
        val mediaSource = buildMediaSource(uri, defaultHttpDataSourceFactory, null)
        setMediaSource(mediaSource)
        playWhenReady = true
        prepare()
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun buildMediaSource(
    uri: Uri,
    defaultHttpDataSourceFactory: DefaultHttpDataSource.Factory,
    overrideExtension: String?,
): MediaSource {
    val type = Util.inferContentType(uri, overrideExtension)
    return when (type) {
        C.CONTENT_TYPE_DASH -> DashMediaSource.Factory(defaultHttpDataSourceFactory!!)
            .createMediaSource(MediaItem.fromUri(uri))

        C.CONTENT_TYPE_SS -> SsMediaSource.Factory(defaultHttpDataSourceFactory!!)
            .createMediaSource(MediaItem.fromUri(uri))

        C.CONTENT_TYPE_HLS -> HlsMediaSource.Factory(defaultHttpDataSourceFactory!!)
            .createMediaSource(MediaItem.fromUri(uri))

        C.CONTENT_TYPE_OTHER -> ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory!!)
            .createMediaSource(MediaItem.fromUri(uri))

        else -> {
            throw IllegalStateException("Unsupported type: $type")
        }
    }
}

@Composable
fun createPlayerView(player: Player?): PlayerView {
    val context = LocalContext.current
    val playerView = remember {
        PlayerView(context).apply {
            this.player = player
        }
    }
    DisposableEffect(key1 = player) {
        playerView.player = player
        onDispose {
            playerView.player = null
        }
    }
    return playerView

}