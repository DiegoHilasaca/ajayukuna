package com.ajayu.newproyect

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_exo.*
import kotlinx.android.synthetic.main.custom_controller.*
import java.io.File
const val PATH_APP= "Ajayukuna"

class ExoActivity : AppCompatActivity(),Player.EventListener {

    private lateinit var videoPath : String
    private lateinit var simpleExoPlayer : SimpleExoPlayer
    private lateinit var uri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exo)


        val intent = intent
        videoPath = intent.getStringExtra("videoPath")
        Log.e("videoPath",videoPath)
        val videoTitleString= PATH_APP+File.separator+videoPath.substringAfterLast("/").substring(2)
        video_tittle.text= videoTitleString

        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        playerView.player=simpleExoPlayer
        uri = Uri.parse(videoPath)
        initializePlayer()
    }
    private fun initializePlayer() {
        val mediaSource = buildMediaSource(uri)
        simpleExoPlayer.playWhenReady = playWhenReady
        simpleExoPlayer.seekTo(currentWindow, playbackPosition.toLong())
        simpleExoPlayer.addListener(this)
        simpleExoPlayer.prepare(mediaSource!!, false, false)

    }

    private fun buildMediaSource(uri: Uri): MediaSource? {
        val dataSourceFactory: DataSource.Factory =  DefaultDataSourceFactory(this, "Ajayukuna")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        val stateString = when (playbackState) {
            ExoPlayer.STATE_IDLE-> "ExoPlayer.STATE_IDLE      -"
            ExoPlayer.STATE_BUFFERING->{
                "ExoPlayer.STATE_BUFFERING -"
            }
            ExoPlayer.STATE_READY-> "ExoPlayer.STATE_READY     -"
            ExoPlayer.STATE_ENDED-> "ExoPlayer.STATE_ENDED     -"
            else -> "UNKNOWN_STATE             -"
        }
        Log.e("Exoplayer", "Event changed state to " + stateString
                + " playWhenReady: " + playWhenReady+"///"+ExoPlayer.STATE_READY.toString())
        Log.e("STATE_BUFFERING",simpleExoPlayer.playbackState.toString())
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }

    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT < 24) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }

    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }


    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0

    private fun releasePlayer() {
        if (simpleExoPlayer != null) {
            playWhenReady = simpleExoPlayer.playWhenReady
            playbackPosition = simpleExoPlayer.currentPosition.toInt()
            currentWindow = simpleExoPlayer.currentWindowIndex
            simpleExoPlayer.removeListener(this)
            simpleExoPlayer.stop()
            simpleExoPlayer.release()
        }
    }

    private fun hideSystemUi() {
        playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
}