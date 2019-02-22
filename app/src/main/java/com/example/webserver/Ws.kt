package com.example.webserver

import android.util.Log
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD
import java.io.IOException

class Ws(handshakeRequest : NanoHTTPD.IHTTPSession?) : NanoWSD.WebSocket(handshakeRequest) {
    override fun onOpen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClose(code: NanoWSD.WebSocketFrame.CloseCode?, reason: String?, initiatedByRemote: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPong(pong: NanoWSD.WebSocketFrame?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessage(message: NanoWSD.WebSocketFrame?) {
        Log.d("WebSocket_Testing", message?.textPayload)
    }

    override fun onException(exception: IOException?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}