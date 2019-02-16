package com.example.webserver

import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

class WebServer (val port_num : Int) : NanoHTTPD(port_num) {


    override fun serve(session: IHTTPSession?): Response {

        var reply : Response = when(session?.uri){
            "/" -> newFixedLengthResponse("WHAT UP")
            "/music" -> getSound()
            "/hi" -> newFixedLengthResponse("pls kill me now")
            else -> newFixedLengthResponse("DEFAULT RESPONSE")
        }

        return reply

    }

    private fun getSound() : Response{
        var myInput: InputStream? = null
        try {
            var file = File(allAudios.AudioList[1]._path)
            myInput = FileInputStream(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return createResponse(NanoHTTPD.Response.Status.OK, "audio/mpeg", myInput)
    }

    //Announce that the file server accepts partial content requests
    private fun createResponse(
        status: Response.Status, mimeType: String,
        message: InputStream?
    ): NanoHTTPD.Response {
        return NanoHTTPD.newChunkedResponse(status, mimeType, message)
    }
}