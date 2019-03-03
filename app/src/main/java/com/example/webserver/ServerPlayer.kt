package com.example.webserver

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.util.Log

object ServerPlayer {

    private const val LOG_TAG = "playerObject"
    var musicPlayer: MediaPlayer? = MediaPlayer()
    var handler: Handler = Handler()
    var mRunnable = Runnable {
        Log.d("handlerTest","TriggeredTime: ${System.currentTimeMillis()}")
        musicPlayer?.start()
    }

    fun StartMusic(){
        Log.d("handlerTest","startTime: ${System.currentTimeMillis()}")
        handler.postDelayed(
            mRunnable, // Runnable
           5000
        )
        GroupOwner.SyncCurrentPosition()
        GroupOwner.sendPlayToAllClients()

    }

    fun PauseMusic(){
        musicPlayer?.pause()
        GroupOwner.sendPauseToAllClients()

    }


    fun initializeMusicPlayer(context: Context){
            musicPlayer?.setDataSource(context, Uri.parse(allAudios.AudioList[1]._path))
            musicPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            musicPlayer?.prepare()
            Log.d(LOG_TAG, "prepared")

    }
}