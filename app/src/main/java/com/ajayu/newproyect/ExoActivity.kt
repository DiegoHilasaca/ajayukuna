package com.ajayu.newproyect

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.navigation.navArgs
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

class ExoActivity : AppCompatActivity(),Player.EventListener {

    lateinit var videoPath : String
    lateinit var simpleExoPlayer : SimpleExoPlayer
    lateinit var uri: Uri
    private val PATH_APP= "Ajayukuna"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exo)


        val intent = intent
        videoPath = intent.getStringExtra("videoPath")
        Log.e("videoPath",videoPath)
        video_tittle.text= PATH_APP+File.separator+videoPath.substringAfterLast("/").substring(2)

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
        var stateString=""
        stateString = when (playbackState) {
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
        if (Util.SDK_INT < 24 || simpleExoPlayer == null) {
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

    fun releasePlayer() {
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