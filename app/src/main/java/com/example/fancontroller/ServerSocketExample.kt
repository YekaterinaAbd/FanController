package com.example.fancontroller;

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket


class ServerSocketExample(private val logger: Logger) {
    private val port = 7777
    private lateinit var server: ServerSocket

    init {
        try {
            server = ServerSocket(port)
        } catch (e: Exception) {
            logger.setLogText("Failed to connect!")
        }
    }

    fun handleConnection() {
        logger.setLogText("Waiting for client message...");

        // The server do a loop here to accept all connection initiated by the
        // client application.
        while (true) {
            try {
                val client: Socket = server.accept()
                ConnectionHandler(client, logger)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class ConnectionHandler(private val client: Socket, private val logger: Logger) : Runnable {

    init {
        val t = Thread(this)
        t.start()
    }

    override fun run() {
        try {
            // Read a message sent by client application
            val ois = ObjectInputStream(client.getInputStream())
            val message = ois.readObject()

            logger.setLogText("Message Received: $message")

            // Send a response information to the client application
            val oos = ObjectOutputStream(client.getOutputStream())
            oos.writeObject("Hi...")

            ois.close()
            oos.close()
            client.close()

            logger.setLogText("Waiting for client message...");
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}