package com.example.MuSyncTest.Servers

import com.example.MuSyncTest.ModifiedLibrary.NanoWSD


class WebSocketServer (val port_num : Int = 8090) : NanoWSD(port_num) {

    override fun openWebSocket(handshake: IHTTPSession?): WebSocket {
        return Ws(handshake)
    }

}