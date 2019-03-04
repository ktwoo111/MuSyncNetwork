package com.example.webserver

import android.util.Log

object GroupOwner {
    var clients :MutableList<Ws> = mutableListOf<Ws>()
    val httpStuff : HttpServer = HttpServer()
    val websocketStuff : WebSocketServer = WebSocketServer()

    fun clientAdded(a : Ws){
        clients.add(a)
    }

    fun clientRemoved(a : Ws){
        clients.remove(a)
    }

    fun SyncCurrentPositiontoAllClients() : Boolean{
        try {
            for (client in clients) {
                client.send("0;${ServerPlayer.musicPlayer?.currentPosition};${System.currentTimeMillis()}")
            }
            return true
        }
        catch (e: Exception) {
            return false
        }
    }
    fun sendPlayToAllClients(startTime: Long, delay: Long){
        for (client in clients) {
            client.send("1;${startTime};${delay}")
            Log.d("handlerTest","startTimeToClient: ${startTime}, systemTime: ${System.currentTimeMillis()}")
        }

    }
    fun sendPauseToAllClients(){
        for (client in clients) {
            client.send("2")
        }
    }
    fun displayNumOfConnections() : Int {
        return clients.size
    }

    fun RunServer(){
        httpStuff.start() //starting http file server on port 8080
        websocketStuff.start() //starting websocket serve ron port 8090
    }
}