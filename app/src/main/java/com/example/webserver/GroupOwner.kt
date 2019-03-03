package com.example.webserver

import android.util.Log

object GroupOwner {
    var clients :MutableList<Ws> = mutableListOf<Ws>()
    val yo : WebServer = WebServer(8080)

    fun clientAdded(a : Ws){
        clients.add(a)
    }

    fun clientRemoved(a : Ws){
        clients.remove(a)
    }

    fun SyncCurrentPositiontoAllClients() : Boolean{
        try {
            for (client in clients) {
                client.send("0;${ServerPlayer.musicPlayer?.currentPosition}")
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
        yo.start()
    }
}