package com.apolis.servicedemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlin.random.Random

class MusicService : Service() {
    var isMediaPlayerReady = false
    var musicControlCmd = ""
    lateinit var musicBinder: MusicBinder
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startAsForegroundService()
        }
    }

    private fun startAsForegroundService() {

        val id = Random.nextInt(50000)

        val playIntent = Intent(baseContext, MusicService::class.java)
        playIntent.putExtra("cmd", "play")
        val piPlay = PendingIntent.getService(this, Random.nextInt(50000), playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val pauseIntent = Intent(baseContext, MusicService::class.java)
        pauseIntent.putExtra("cmd", "pause")
        val piPause = PendingIntent.getService(this, Random.nextInt(50000), pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val stopIntent = Intent(baseContext, MusicService::class.java)
        stopIntent.putExtra("cmd", "stop")
        val piStop = PendingIntent.getService(this, Random.nextInt(50000), stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val contentIntent = Intent(baseContext, MainActivity::class.java)
        val piActivity = PendingIntent.getActivity(baseContext, id, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, "MusicPlayer")
            .setContentTitle("Now Playing - Meri Kahani")
            .setContentText("Media Player is playing music in background")
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_player))
            .setSmallIcon(R.drawable.ic_small_icon)
            .addAction(R.drawable.ic_small_icon, "Play", piPlay)
            .addAction(R.drawable.ic_small_icon, "Pause", piPause)
            .addAction(R.drawable.ic_small_icon, "Stop", piStop)
            .setContentIntent(piActivity)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("MusicPlayer", "Music Player", NotificationManager.IMPORTANCE_HIGH)
            nm.createNotificationChannel(channel)
        }

        startForeground(id, notification)

    }

    override fun onBind(intent: Intent): IBinder? {

        if(this::musicBinder.isInitialized) {
            return musicBinder
        }
        musicBinder = MusicBinder()
        return musicBinder

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
        return START_STICKY
    }

    private fun stopMusic() {

        mediaPlayer.stop()
        mediaPlayer.release()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
            stopSelf()
        } else {
            stopSelf()
        }

    }

    private fun playMusic() {
        if(this::mediaPlayer.isInitialized && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

    }

    private fun pauseMusic() {
        if(this::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
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