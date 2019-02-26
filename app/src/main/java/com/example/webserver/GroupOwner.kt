package com.example.webserver

object GroupOwner {
    var clients :MutableList<Ws> = mutableListOf<Ws>()
    val yo : WebServer = WebServer(8080)

    fun clientAdded(a : Ws){
        clients.add(a)
    }

    fun clientRemoved(a : Ws){
        clients.remove(a)
    }

    fun sendCommandToAllClients(input: String){
        for (client in clients){
            client.send(input)
        }
    }
    fun displayNumOfConnections() : Int {
        return clients.size
    }

    fun RunServer(){
        yo.start()
    }
}