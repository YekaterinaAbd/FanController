package com.example.fancontroller

import java.lang.Exception
import java.net.Socket

interface Logger {
    fun setLogText(text: String)
}

class SocketClient(private val ip: String, private val logger: Logger) {

    private val port = 1234
    private lateinit var socket: Socket

    fun runClient(){
        try {
            socket = Socket(ip, port)

        } catch (e: Exception){
            logger.setLogText("Connection failed!")
        }
    }
}