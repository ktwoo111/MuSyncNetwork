package com.example.webserver


import android.util.Log
import java.io.IOException

class Ws(handshakeRequest : NanoHTTPD.IHTTPSession?) : NanoWSD.WebSocket(handshakeRequest) {
    override fun onOpen() {
        Log.d("WebSocket_Testing", "Attempts were made")
        ServerHolder.clientAdded(this)
        Log.d("WebSocket_Testing", "number of connections: ${ServerHolder.displayNumOfConnections()}")
    }

    override fun onClose(code: NanoWSD.WebSocketFrame.CloseCode?, reason: String?, initiatedByRemote: Boolean) {
        Log.d("WebSocket_Training", "BYE")
        ServerHolder.clientRemoved(this)
    }

    override fun onPong(pong: NanoWSD.WebSocketFrame?) {
        Log.d("WebSocket_Training","PONG")
    }

    override fun onMessage(message: NanoWSD.WebSocketFrame?) {
        Log.d("WebSocket_Testing", message?.textPayload)

    }

    override fun onException(exception: IOException?) {
        Log.d("WebSocket_Training", "EXCEPTION")
    }
}