package com.example.clientmusicplayer

import android.os.Handler
import android.util.Log
import com.example.MuSyncTest.MainActivity
import com.example.MuSyncTest.MusicPlayer.ClientPauseMusic
import com.example.MuSyncTest.MusicPlayer.ClientStartMusic
import com.example.MuSyncTest.MusicPlayer.ClientSyncMusic
import com.example.MuSyncTest.MusicPlayer.getPosition
import com.instacart.library.truetime.TrueTime
import okhttp3.*
import okio.ByteString
import kotlin.math.abs


class ClientWebSocket() : WebSocketListener() {

    companion object{
        private const val LOG_TAG= "ClientWebSocket"
        private const val NORMAL_CLOSURE_STATUS = 99
        private const val SYNC = "0"
        private const val PLAY = "1"
        private const val PAUSE = "2"

    }

    var messageFromServer: List<String>? = null
    var currentCode : String?  = ""
    var handler: Handler = Handler()
    var mRunnable = Runnable {
       ClientStartMusic()
        Log.d(LOG_TAG,"TriggeredTime: ${TrueTime.now().time}")
    }

    fun retrieveMessage(t: String?){
        messageFromServer = t?.split(";")
    }
    fun decipherMessage(){
        currentCode = messageFromServer?.get(0)
      if (currentCode == SYNC){
          setSync(messageFromServer?.get(1)?.toLong() as Long, messageFromServer?.get(2)?.toInt() as Int)

      }
      else if (currentCode == PLAY){
            setDelayedPlay(messageFromServer?.get(1)?.toLong() as Long, messageFromServer?.get(2)?.toLong() as Long)

        }
        else if (currentCode == PAUSE){
            setPause()

        }

    }

    fun setPause(){
        Log.d(LOG_TAG, "pausing music")
        ClientPauseMusic()
        /*
        Log.d(LOG_TAG, "pausing music done; going to perform sync button")
        val url = activity?.httpStuff+activity?.wifi_address+":8080/position"
        val request_position = Request.Builder().url(url).build()
        var startTime = TrueTime.now().time
        var hi = object: Callback {
            override fun onResponse(call: Call?, response: Response?){
                val body = response?.body()?.string()
                var position: Long? = body?.toLong()
                activity?.musicPlayer?.seekTo(position?.toInt() as Int)
                Log.d(LOG_TAG,"player Position: ${activity?.musicPlayer?.currentPosition}")
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d(LOG_TAG, "not good stuff for http for pausing sync")
            }


        }
        activity.client?.newCall(request_position)?.enqueue(hi)
        Log.d(LOG_TAG, "sync button done")
        */
        Log.d(LOG_TAG, "paused")
    }
    fun setDelayedPlay(systemTimeFromServer: Long, delayTime: Long){
        var clientTime = TrueTime.now().time
        var diff = clientTime - systemTimeFromServer
        var newDelay = delayTime - abs(diff)
        Log.d(LOG_TAG, "client: $clientTime, server: $systemTimeFromServer, diff: $diff")
        handler.postDelayed(mRunnable,newDelay)
        Log.d(LOG_TAG,"delayed play set")
    }

    fun setSync(systemTimeFromServer: Long, timePosition: Int){
        var clientTime = TrueTime.now().time
        var diff = clientTime - systemTimeFromServer
        var newSeekTime : Int = timePosition + diff.toInt()
        ClientSyncMusic(newSeekTime)
        Log.d(LOG_TAG,"newSeekTime: $newSeekTime ,diff: $diff,  musicPosition: ${getPosition()}")
    }
    override fun onOpen(webSocket: WebSocket, response: Response) {

        Log.d(LOG_TAG, "connected to master")

    }

    override fun onMessage(webSocket: WebSocket?, text: String?){
        Log.d(LOG_TAG, "message: $text")
        Log.d(LOG_TAG, "currentTime: ${System.currentTimeMillis()}")
        retrieveMessage(text)
        decipherMessage()

    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
    }

}