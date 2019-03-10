package com.example.clientmusicplayer

import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.webserver.MainActivity
import okhttp3.*
import okio.ByteString
import java.io.IOException
import kotlin.math.abs


class ClientWebSocket(var activity : MainActivity) : WebSocketListener() {

    var messageFromServer: List<String>? = null
    var currentCode : String?  = ""
    var handler: Handler = Handler()
    var mRunnable = Runnable {
        Log.d(LOG_TAG,"TriggeredTime: ${System.currentTimeMillis()}")
        activity?.StartMusic()
        Toast.makeText(activity, "${System.currentTimeMillis()}", Toast.LENGTH_LONG).show()
    }

    fun retrieveMessage(t: String?){
        messageFromServer = t?.split(";")
    }
    fun decipherMessage(){
        currentCode = messageFromServer?.get(0)
       if (currentCode == PLAY){
            setDelayedPlay(messageFromServer?.get(1)?.toLong() as Long, messageFromServer?.get(2)?.toLong() as Long)

        }
        else if (currentCode == PAUSE){
            setPause()

        }

    }

    fun setPause(){
        Log.d(LOG_TAG, "pausing music")
        activity?.PauseMusic()
        Log.d(LOG_TAG, "pausing music done; going to perform sync button")
        val url = activity?.httpStuff+activity?.wifi_address+":8080/position"
        val request_position = Request.Builder().url(url).build()
        var startTime = System.currentTimeMillis()
        var hi = object: Callback {
            override fun onResponse(call: Call?, response: Response?){ //this is being run on a different thread, so you have to trigger UIthread to make changes to UI with updated info
                val body = response?.body()?.string()
                var position: Long? = body?.toLong()
                activity?.musicPlayer?.seekTo(position?.toInt() as Int)
                Log.d(LOG_TAG,"player Position: ${activity?.musicPlayer?.currentPosition}")
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d(LOG_TAG, "not good stuff for http for pausing sync")
            }


        }
        ClientActivity.client?.newCall(request_position)?.enqueue(hi)
        Log.d(LOG_TAG, "sync button done")
        Log.d(LOG_TAG, "paused")
    }
    fun setDelayedPlay(systemTimeFromServer: Long, delayTime: Long){
        var clientTime = System.currentTimeMillis()
        var diff = System.currentTimeMillis() - systemTimeFromServer
        var newDelay = delayTime - abs(diff)
        Log.d(LOG_TAG, "client: $clientTime, server: $systemTimeFromServer, diff: $diff")
        handler.postDelayed(mRunnable,newDelay)






    }



    companion object{
        private const val LOG_TAG= "ClientWebSocket"
        private const val NORMAL_CLOSURE_STATUS = 99
        private const val SYNC = "0"
        private const val PLAY = "1"
        private const val PAUSE = "2"

    }
    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d(LOG_TAG, "connected to master")

    }

    override fun onMessage(webSocket: WebSocket?, text: String?){
        Log.d(LOG_TAG, "message: $text")
        Log.d(LOG_TAG, "currentTIme: ${System.currentTimeMillis()}")
        retrieveMessage(text)
        decipherMessage()

    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
    }

}