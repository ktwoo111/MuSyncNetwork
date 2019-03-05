package com.example.webserver.Servers

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import com.example.webserver.AudioRetrieval.allAudios
import org.jetbrains.anko.doAsync

object ServerAndMusicHolder {
    var musicIndex = 500
    var clients :MutableList<Ws> = mutableListOf<Ws>()
    val httpStuff : HttpServer = HttpServer()
    val websocketStuff : WebSocketServer = WebSocketServer()

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
            ServerAndMusicHolder.sendPlayToAllClients(startTime, delay)
        }
    }

    fun PauseMusic(){
        musicPlayer?.pause()
        doAsync {
            ServerAndMusicHolder.sendPauseToAllClients()
        }

    }


    fun initializeMusicPlayer(context: Context, wifi_address: String){
        musicPlayer?.setDataSource(allAudios.AudioList[musicIndex]._path)
        musicPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        musicPlayer?.prepare()

    }

    fun clientAdded(a : Ws){
        clients.add(a)
    }

    fun clientRemoved(a : Ws){
        clients.remove(a)
    }

    fun SyncCurrentPositiontoAllClients() : Boolean{
        try {
            for (client in clients) {
                client.send("0;${musicPlayer?.currentPosition};${System.currentTimeMillis()}")
            }
            return true
        }
        catch (e: Exception) {
            return false
        }
    }
    fun sendPlayToAllClients(startTime: Long, delay: Long){
        for (client in clients) {
            client.send("1;${startTime};${delay}")
            Log.d("handlerTest","startTimeToClient: ${startTime}, systemTime: ${System.currentTimeMillis()}")
        }

    }
    fun sendPauseToAllClients(){
        for (client in clients) {
            client.send("2")
        }
    }
    fun displayNumOfConnections() : Int {
        return clients.size
    }

    fun RunServer(){
        httpStuff.start() //starting http file server on port 8080
        websocketStuff.start() //starting websocket serve ron port 8090
    }
    fun StopServer(){
        httpStuff.closeAllConnections()
        httpStuff.stop()

        websocketStuff.closeAllConnections()
        websocketStuff.stop()
    }
}