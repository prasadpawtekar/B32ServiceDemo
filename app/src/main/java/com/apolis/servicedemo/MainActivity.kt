package com.apolis.servicedemo

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import com.apolis.servicedemo.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    lateinit var musicBinder: MusicService.MusicBinder
    var isPlayerStopped = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEvents()
    }

    private fun setupEvents() {

        binding.btnStartMusic.setOnClickListener {
            val sIntent = Intent(baseContext, MusicService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(sIntent)
            } else {
                startService(sIntent)
            }

            bindService(sIntent, serviceConnection, BIND_AUTO_CREATE)

        }

        binding.btnPause.setOnClickListener {
            if (this::musicBinder.isInitialized) {
                musicBinder.pause()
            }
        }

        binding.btnPlay.setOnClickListener {
            if (this::musicBinder.isInitialized) {
                musicBinder.play()
            }
        }

        binding.btnStop.setOnClickListener {
            if (this::musicBinder.isInitialized) {
                musicBinder.stop()
                isPlayerStopped = true
            }
        }

        binding.sbCurrentPosition.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && this@MainActivity::musicBinder.isInitialized) {
                    musicBinder.setCurrentSeekPosition(progress)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            musicBinder = binder as MusicService.MusicBinder
            Log.d("ServiceDemo", "onServiceConnected: ")
            musicBinder.start()
            isPlayerStopped = false
            setupSeekPositionListener()

        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }
    }

    fun setupSeekPositionListener() {

        lifecycleScope.launch(Dispatchers.Main) {
            var totalTime = -1
            do {
                delay(1000)
                totalTime = musicBinder.getTotalTime()
            } while (totalTime == -1)

            binding.sbCurrentPosition.max = totalTime

            while (!isPlayerStopped) {
                val cp = musicBinder.getCurrentSeekPosition()

                if (cp != -1) {
                    binding.sbCurrentPosition.progress = cp
                }
                delay(1000)
            }
        }
    }
}