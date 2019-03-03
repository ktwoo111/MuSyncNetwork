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
        GroupOwner.sendCommandToAllClients("1;${System.currentTimeMillis()};${musicPlayer?.currentPosition}")
        musicPlayer?.start()
        Log.d("MUSIC_CURRENT", "${musicPlayer?.currentPosition}")
    }

    fun PauseMusic(){
        GroupOwner.sendCommandToAllClients("2;${System.currentTimeMillis()};${musicPlayer?.currentPosition}")
        musicPlayer?.pause()
    }


    fun initializeMusicPlayer(context: Context){
            musicPlayer?.setDataSource(context, Uri.parse(allAudios.AudioList[1]._path))
            musicPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            musicPlayer?.prepare()
            Log.d(LOG_TAG, "prepared")

    }
}