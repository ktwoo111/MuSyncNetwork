package com.example.MuSyncTest

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import com.example.MuSyncTest.AudioRetrieval.allAudios
import com.example.MuSyncTest.Servers.ServerHolder
import com.instacart.library.truetime.TrueTime
import okhttp3.OkHttpClient
import org.jetbrains.anko.doAsync

object MusicPlayer {
    private const val LOG_TAG = "448.MUSICPLAYER"
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



    fun getPosition(): Int?{
        return musicPlayer?.currentPosition

    }

    fun HostStartMusic(){
        var startTime = TrueTime.now().time
        Log.d(LOG_TAG,"startTime for handler to initiate: ${startTime}")
        handler.postDelayed(
            mRunnable, // Runnable
            delay
        )
        doAsync {
            ServerHolder.sendPlayToAllClients(startTime, delay)
        }
    }

    fun HostPauseMusic(){
        musicPlayer?.pause()
        doAsync {
            ServerHolder.sendPauseToAllClients()
        }

    }

    fun HostSyncMusic(){
        var startTime = TrueTime.now().time
        doAsync {
            ServerHolder.sendSyncToAllClients( startTime, musicPlayer?.currentPosition)
        }


    }

    fun ClientSyncMusic(time: Int){
        musicPlayer?.seekTo(time)

    }

    fun ClientStartMusic(){
        musicPlayer?.start()
    }

    fun ClientPauseMusic(){
        musicPlayer?.pause()

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
}