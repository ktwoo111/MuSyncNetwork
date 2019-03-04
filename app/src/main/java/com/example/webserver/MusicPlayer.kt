package com.example.webserver

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.util.Log
import org.jetbrains.anko.doAsync


object MusicPlayer {

    private const val LOG_TAG = "playerObject"
    var musicPlayer: MediaPlayer? = MediaPlayer()
    var handler: Handler = Handler()
    var delay: Long = 1500 //delay of amount
    var mRunnable = Runnable {
        Log.d("handlerTest","TriggeredTime: ${System.currentTimeMillis()}")
        musicPlayer?.start()
    }

    fun StartMusic(){
        var startTime = System.currentTimeMillis()
        Log.d("handlerTest","startTime: ${System.currentTimeMillis()}")
        handler.postDelayed(
            mRunnable, // Runnable
           delay
        )
        doAsync {
            ServerHolder.sendPlayToAllClients(startTime, delay)
        }
    }

    fun PauseMusic(){
        musicPlayer?.pause()
        doAsync {
            ServerHolder.sendPauseToAllClients()
        }

    }


    fun initializeMusicPlayer(context: Context, wifi_address: String){
            musicPlayer?.setDataSource(allAudios.AudioList[476]._path)
            musicPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            musicPlayer?.prepare()
            Log.d(LOG_TAG, "prepared")

    }
}