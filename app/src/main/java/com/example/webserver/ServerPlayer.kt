package com.example.webserver

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log

object ServerPlayer {

    private const val LOG_TAG = "playerObject"
    var musicPlayer: MediaPlayer? = MediaPlayer()

    fun StartMusic(){
        musicPlayer?.start()
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