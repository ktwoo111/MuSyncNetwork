package com.example.webserver.Servers

import com.example.webserver.ModifiedLibrary.NanoWSD


class WebSocketServer (val port_num : Int = 8090) : NanoWSD(port_num) {

    override fun openWebSocket(handshake: IHTTPSession?): WebSocket {
        return Ws(handshake)
    }

}