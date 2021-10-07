package com.apolis.servicedemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.apolis.servicedemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEvents()
    }

    private fun setupEvents() {

        binding.btnStartMusic.setOnClickListener {
            val sIntent = Intent(baseContext, MusicService::class.java)
            sIntent.putExtra("cmd", "start")
            startService(sIntent)
        }

        binding.btnPause.setOnClickListener {
            val sIntent = Intent(baseContext, MusicService::class.java)
            sIntent.putExtra("cmd", "pause")
            startService(sIntent)
        }

        binding.btnPlay.setOnClickListener {
            val sIntent = Intent(baseContext, MusicService::class.java)
            sIntent.putExtra("cmd", "play")
            startService(sIntent)
        }

        binding.btnStop.setOnClickListener {
            val sIntent = Intent(baseContext, MusicService::class.java)
            sIntent.putExtra("cmd", "stop")
            startService(sIntent)
        }

    }
}