package com.example.webserver

import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream



class HttpServer (val port_num : Int = 8080) : NanoHTTPD(port_num) {

    override fun serve(session: IHTTPSession?): Response {

        if (session?.uri == "/"){
            return newFixedLengthResponse("WHAT UP")
        }
        else if (session?.uri?.contains("/music") == true){
            return getSound(session?.uri)
        }
        else if (session?.uri?.contains("/title") == true){
            return newFixedLengthResponse(allAudios.AudioList[1]._name)
        }
        else {
            return newFixedLengthResponse("DEFAULT RESPONSE")

        }
    }


    //cutomized functions TAEWOO KIM
    private fun getSound(input: String?) : Response{
        var t = input?.split("/")
        Log.d("HTTPSERVER", t.toString())

        var myInput: InputStream? = null
        try {
            var file = File(allAudios.AudioList[t?.get(2)?.toInt() as Int]._path)
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