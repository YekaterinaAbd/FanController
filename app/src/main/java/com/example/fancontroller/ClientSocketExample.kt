package com.example.fancontroller;

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.Socket

class ClientSocketExample(logger: Logger) {
    init {
        try {
            // Create a connection to the server socket on the server application
            val host = InetAddress.getLocalHost()
            val socket = Socket(host.hostName, 7777)

            // Send a message to the client application
            val oos = ObjectOutputStream(socket.getOutputStream())
            oos.writeObject("Hello There...")

            // Read and display the response message sent by server application
            val ois = ObjectInputStream(socket.getInputStream())
            val message = ois.readObject()
            logger.setLogText("Message: $message")
            ois.close()
            oos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}