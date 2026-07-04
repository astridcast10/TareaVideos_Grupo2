package com.grupo2.videoapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.VideoView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var btnRecord: Button
    private lateinit var btnSave: Button
    private var videoUri: Uri? = null

    private val recordVideoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            videoUri = result.data?.data
            videoUri?.let {
                videoView.setVideoURI(it)
                videoView.start()
                btnSave.isEnabled = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoView = findViewById(R.id.videoView)
        btnRecord = findViewById(R.id.btnRecord)
        btnSave = findViewById(R.id.btnSave)

        btnRecord.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            recordVideoLauncher.launch(intent)
        }

        btnSave.setOnClickListener {
            saveVideoMetadata()
        }
    }

    private fun saveVideoMetadata() {
        val uriString = videoUri?.toString()
        if (uriString != null) {
            val videoEntity = VideoEntity(
                uri = uriString,
                timestamp = System.currentTimeMillis()
            )

            lifecycleScope.launch {
                val db = VideoDatabase.getDatabase(this@MainActivity)
                db.videoDao().insertVideo(videoEntity)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Video guardado en la base de datos", Toast.LENGTH_SHORT).show()
                    btnSave.isEnabled = false
                }
            }
        } else {
            Toast.makeText(this, "No hay video para guardar", Toast.LENGTH_SHORT).show()
        }
    }
}
