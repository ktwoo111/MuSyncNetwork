package com.example.webserver

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.clientmusicplayer.ClientWebSocket
import com.example.webserver.AudioRetrieval.allAudios
import com.example.webserver.Servers.ServerAndMusicHolder
import okhttp3.*
import org.jetbrains.anko.doAsync
import java.io.IOException


class MainActivity : AppCompatActivity() {
    companion object{
        private const val LOG_TAG = "448.MainActivity"

    }
    var isHost = false
    var musicIndex = -1
    var musicPlayer: MediaPlayer? = MediaPlayer()
    var handler: Handler = Handler()
    var delay: Long = 1500 //delay of amount
    var mRunnable = Runnable {
        Log.d(LOG_TAG,"TriggeredTime for audio start: ${System.currentTimeMillis()}")
        musicPlayer?.start()
    }

    //variables mainly for Client
    val musicSuffix = ":8080/music/"
    val titleSuffix = ":8080/title/"
    val positionSuffix = ":8080/position"
    var httpStuff = "http://"
    var wsStuff = "ws://"
    var wifi_address = ""
    val client: OkHttpClient? = OkHttpClient()

    fun HostStartMusic(){
        var startTime = System.currentTimeMillis()
        Log.d(LOG_TAG,"startTime for handler to initiate: ${System.currentTimeMillis()}")
        handler.postDelayed(
            mRunnable, // Runnable
            delay
        )
        doAsync {
            ServerAndMusicHolder.sendPlayToAllClients(startTime, delay)
        }
    }

    fun HostPauseMusic(){
        musicPlayer?.pause()
        doAsync {
            ServerAndMusicHolder.sendPauseToAllClients()
        }

    }

    fun ClientStartMusic(){//TODO
        var startTime = System.currentTimeMillis()
        Log.d(LOG_TAG,"startTime for handler to initiate: ${System.currentTimeMillis()}")
        handler.postDelayed(
            mRunnable, // Runnable
            delay
        )
        doAsync {
            ServerAndMusicHolder.sendPlayToAllClients(startTime, delay)
        }
    }

    fun ClientPauseMusic(){//TODO
        musicPlayer?.pause()
        doAsync {
            ServerAndMusicHolder.sendPauseToAllClients()
        }

    }

    fun initializeHostMusicPlayer(){
        musicPlayer?.setDataSource(allAudios.AudioList[musicIndex]._path)
        musicPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        musicPlayer?.prepareAsync()
    }

    fun initializeClientMusicPlayer(){
        musicPlayer?.setDataSource(httpStuff+wifi_address+musicSuffix+musicIndex)
        musicPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        musicPlayer?.prepareAsync()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    123)
            }
        } else {

        }

        //getting wifi address
        val wifiMan = this.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInf = wifiMan.connectionInfo
        val ipAddress = wifiInf.ipAddress
        val ip = String.format(
            "%d.%d.%d.%d", ipAddress and 0xff, ipAddress shr 8 and 0xff, ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )
        wifi_address_display.text = ip



        //initialize player
        ServerAndMusicHolder.initializeHostMusicPlayer(applicationContext, ip)


        //display title_text
        title_text.text = allAudios.AudioList[ServerAndMusicHolder.musicIndex]._name



        /*
        sync_button.setOnClickListener{
            if(MusicPlayer.syncMusic()) {
                Toast.makeText(this, "Sync", Toast.LENGTH_SHORT).show()
            }
        }
        */
        //button listener
        play_button.setOnClickListener{
                ServerAndMusicHolder?.StartMusic()
                Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show()
        }
        pause_button.setOnClickListener{
            ServerAndMusicHolder.PauseMusic()
            Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show()
        }


    }

    fun InitializationForHost(){
        //fetching all the audio files in phone
        allAudios.getAllAudioFromDevice(this)
        audio_size_display.text = allAudios.AudioList.size.toString()

        //start server
        ServerAndMusicHolder.RunServer()


    }

    fun InitializationForClient(){
        play_button.setOnClickListener{
            musicPlayer?.start()
            Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show()

        }
        pause_button.setOnClickListener{
            musicPlayer?.pause()
            Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show()
        }
        wifi_button.setOnClickListener{
            wifi_address = wifi.text.toString()
            Toast.makeText(this, "WIFI submitted", Toast.LENGTH_SHORT).show()

            //setting up websocket
            Log.d(LOG_TAG, "GETTING TO websocket")
            var web_url = wsStuff+wifi_address+":8090"
            var request_socket = Request.Builder().url(web_url).build()
            var listener = ClientWebSocket(this)
            var ws = client?.newWebSocket(request_socket,listener)
        }

        music_index_button.setOnClickListener {
            musicIndex = music_index.text as Int
            initializeClientMusicPlayer()

            //http request via Okhttp
            Log.d(LOG_TAG,"getting Title from Host")
            val url = httpStuff+wifi_address+titleSuffix+musicIndex.toString()
            val request_title = Request.Builder().url(url).build()
            var startTime = System.currentTimeMillis()
            Log.d(LOG_TAG,"http start: $startTime")
            client?.newCall(request_title)?.enqueue(object: Callback {
                override fun onResponse(call: Call?, response: Response?){ //this is being run on a different thread, so you have to trigger UIthread to make changes to UI with updated info
                    val body = response?.body()?.string()
                    setText(title_text,body as String)
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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            123 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onDestroy(){
        if(isHost) {
            ServerAndMusicHolder.StopServer()
        }
        Log.d("server_status", "onDestroy() called")
        super.onDestroy()
    }
    override fun onStop(){
        Log.d("server_status", "onStop() called")
        super.onStop()

    }

    private fun setText(text: TextView, value: String) {
        runOnUiThread { text.text = value }
    }

}
