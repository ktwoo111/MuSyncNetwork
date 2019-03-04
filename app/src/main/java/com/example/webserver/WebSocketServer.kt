package com.example.webserver

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream


class WebSocketServer (val port_num : Int = 8090) : NanoWSD(port_num) {

    override fun openWebSocket(handshake: IHTTPSession?): WebSocket {
        return Ws(handshake)
    }

}