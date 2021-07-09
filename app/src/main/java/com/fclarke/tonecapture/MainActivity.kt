package com.fclarke.tonecapture

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false

    private lateinit var button_pause_recording: Button
    private lateinit var button_start_recording: Button
    private lateinit var button_stop_recording : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         button_pause_recording = findViewById(R.id.button_pause_recording)
         button_start_recording = findViewById(R.id.button_start_recording)
         button_stop_recording = findViewById(R.id.button_stop_recording)

        mediaRecorder = MediaRecorder()
        //output = Environment.getExternalStorageDirectory().absolutePath + "/StarWars3.wav"//StarWars3.wav
        val file = File(this.externalCacheDir!!.absolutePath, "/StarWars3.mpeg")
        //this.externalMediaDirs
        output = file.absolutePath

        button_start_recording.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(this, permissions, 0)
            } else {
                mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)// 	AMR_NB AMR NB file format//MPEG_4
                        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                mediaRecorder?.setOutputFile(output)
                startRecording()
            }
        }

        button_stop_recording.setOnClickListener{
            stopRecording()
        }
        button_pause_recording.setOnClickListener {
            pauseRecording()
        }
    }

    private fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun pauseRecording() {
        if(state) {
            if(!recordingStopped){
                Toast.makeText(this, "Stopped!", Toast.LENGTH_SHORT).show()
                mediaRecorder?.pause()
                recordingStopped = true
                button_pause_recording.text = "Resume"
            }else{
                resumeRecording()
            }
        }
    }

    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun resumeRecording() {
        Toast.makeText(this, "Resume!", Toast.LENGTH_SHORT).show()
        mediaRecorder?.resume()
        button_pause_recording.text = "Pause"
        recordingStopped = false
    }

    private fun stopRecording(){
        if(state){
            mediaRecorder?.stop()
            mediaRecorder?.release()
            state = false
        }else{
            Toast.makeText(this, "You are not recording right now!", Toast.LENGTH_SHORT).show()
        }
    }
    /*
    fun createAudioRecordBuilder(){
        val record = AudioAttributes.Builder()
            .setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(javax.sound.sampled.FloatControl.Type.SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .build()
    }*/

    fun createAudioRecord(){
        val sampleRate: Int = 44100
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val record = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
    }
}
