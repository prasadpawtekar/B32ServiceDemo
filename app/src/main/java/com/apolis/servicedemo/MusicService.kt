package com.apolis.servicedemo

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class MusicService : Service() {

    var musicControlCmd = ""

    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        musicControlCmd = intent?.extras?.getString("cmd")?:""

        when(musicControlCmd) {
            "start" -> {
                startMusic()
            }
            "pause" -> {
                pauseMusic()
            }
            "play" -> {
                playMusic()
            }
            "stop" -> {
                stopMusic()
            }
        }
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
        }

        mediaPlayer.prepareAsync()


    }

    override fun onDestroy() {
        super.onDestroy()
    }
}