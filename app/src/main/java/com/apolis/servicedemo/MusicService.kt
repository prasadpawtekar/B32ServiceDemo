package com.apolis.servicedemo

import android.app.Service
import android.content.Intent
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class MusicService : Service() {
    var isMediaPlayerReady = false
    var musicControlCmd = ""
    lateinit var musicBinder: MusicBinder
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()

    }

    override fun onBind(intent: Intent): IBinder? {

        if(this::musicBinder.isInitialized) {
            return musicBinder
        }
        musicBinder = MusicBinder()
        return musicBinder

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    private fun stopMusic() {

        mediaPlayer.stop()
        mediaPlayer.release()

    }

    private fun playMusic() {
        if(!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

    }

    private fun pauseMusic() {
        if(mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }

    }

    private fun startMusic() {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource("/mnt/sdcard/Music/meriKahani.mp3")

        mediaPlayer.setOnPreparedListener {
            it.start()
            isMediaPlayerReady = true
        }

        mediaPlayer.prepareAsync()


    }

    override fun onDestroy() {
        super.onDestroy()
    }

    inner class MusicBinder: Binder() {

        fun play() {
            playMusic()
        }

        fun stop() {
            stopMusic()
        }

        fun start() {
            startMusic()
        }

        fun pause() {
            pauseMusic()
        }

        fun getTotalTime(): Int {

            if(isMediaPlayerReady) {
                return mediaPlayer.duration
            }

            return -1
        }

        fun getCurrentSeekPosition(): Int {
            if(isMediaPlayerReady) {
                return mediaPlayer.currentPosition
            }
            return -1
        }

        fun setCurrentSeekPosition(newPosition: Int) {
            mediaPlayer.seekTo(newPosition)
        }

    }
}