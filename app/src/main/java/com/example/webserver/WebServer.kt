package com.example.webserver

import fi.iki.elonen.NanoHTTPD
import java.io.InputStream

class WebServer (val port_num : Int) : NanoHTTPD(port_num) {


    override fun serve(session: IHTTPSession?): Response {
        //var hi : InputStream? = null
        //return NanoHTTPD.newChunkedResponse(Response.Status.OK,"audio/mpeg",hi)

        var reply : Response = when(session?.uri){
            "/" -> newFixedLengthResponse("WHAT UP")
            else -> newFixedLengthResponse("DEFAULT RESPONSE")
        }

        return reply

    }
}