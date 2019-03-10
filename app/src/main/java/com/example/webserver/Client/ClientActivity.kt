package com.example.clientmusicplayer

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.SEEK_PREVIOUS_SYNC
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import android.widget.TextView
import kotlin.math.abs


class ClientActivity : AppCompatActivity() {

    companion object {
        private const val LOG_TAG = ".ClientMusicPlayer"
        val client: OkHttpClient? = OkHttpClient()
    }

    var musicPlayer: MediaPlayer? = null
     var musicURI = ":8080/music"
     var httpStuff = "http://"
     var wsStuff = "ws://"
     var wifi_address = ""
    var musicInitialPlay = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        play_button.setOnClickListener{
            musicPlayer?.start()
            Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show()

        }
        pause_button.setOnClickListener{
            musicPlayer?.pause()
            Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show()
        }
        submit_button.setOnClickListener{
            wifi_address = wifi.text.toString()
            Toast.makeText(this, "SUBMITTED", Toast.LENGTH_SHORT).show()

            //setting up websocket
            Log.d(LOG_TAG, "GETTING TO websocket")
            var web_url = wsStuff+wifi_address+":8090"
            var request_socket = Request.Builder().url(web_url).build()
            var listener = ClientWebSocket(this)
            var ws = client?.newWebSocket(request_socket,listener)
        }

        submit_music_button.setOnClickListener {
            initializeMusicPlayer(music_index.text.toString())

            //http request via Okhttp
            Log.d(LOG_TAG, "GETTING TO title http")
            val url = httpStuff+wifi_address+":8080/title/"+music_index.text.toString()
            val request_title = Request.Builder().url(url).build()
            var startTime = System.currentTimeMillis()
            Log.d(LOG_TAG,"http start: $startTime")
            client?.newCall(request_title)?.enqueue(object: Callback {
                override fun onResponse(call: Call?, response: Response?){ //this is being run on a different thread, so you have to trigger UIthread to make changes to UI with updated info
                    val body = response?.body()?.string()
                    setText(title_text,body as String)
                    var diff = System.currentTimeMillis() - startTime
                    Log.d(LOG_TAG, "got the title ${diff}")
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d(LOG_TAG, "not good stuff for http for title")
                }


            })
        }

        sync_button.setOnClickListener {
            val url = httpStuff+wifi_address+":8080/position"
            val request_position = Request.Builder().url(url).build()
            var startTime = System.currentTimeMillis()
            Log.d(LOG_TAG,"http start: $startTime")
            var hi = object: Callback {
                override fun onResponse(call: Call?, response: Response?){ //this is being run on a different thread, so you have to trigger UIthread to make changes to UI with updated info
                    val body = response?.body()?.string()
                    var diff = System.currentTimeMillis() - startTime
                    var position: Long? = body?.toLong()?.plus(diff)
                    musicPlayer?.seekTo(position?.toInt() as Int)
                    Log.d(LOG_TAG, "got the title ${diff}")
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d(LOG_TAG, "not good stuff for http for title")
                }


            }
                client?.newCall(request_position)?.enqueue(hi)



        }
    }

    fun startMusic(){
        musicPlayer?.start()
    }

    fun pauseMusic(){
        musicPlayer?.pause()

    }
    private fun initializeMusicPlayer(input: String){
        Log.d(".ClientMusicPlayer", wifi_address+musicURI)
        musicPlayer = MediaPlayer()
        if (musicPlayer != null) {
            Log.d(".ClientMusicPlayer", "setDataSource")
            musicPlayer?.setDataSource(httpStuff+wifi_address+musicURI+"/"+input)
            Log.d(".ClientMusicPlayer", "setAudioTYPE")
            musicPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            musicPlayer?.setOnBufferingUpdateListener { mp, percent ->
                if(percent == 100){
                    //ws?.send("BR") //BR means buff ready
                    Log.d(LOG_TAG, "buff: $percent")
                }
            }
            Log.d(".ClientMusicPlayer", "prepare")
            musicPlayer?.prepare()
        }

        }

    private fun setText(text: TextView, value: String) {
        runOnUiThread { text.text = value }
    }



    }

