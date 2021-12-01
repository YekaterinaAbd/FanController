package com.example.fancontroller

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.fancontroller.databinding.FragmentSettingsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

class SettingsFragment : Fragment(R.layout.fragment_settings), Logger {

    private val binding by viewBinding(FragmentSettingsBinding::bind)

    private var socketRunning: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = requireContext().getLightPurple()
        bindViews()
    }

    private fun bindViews() = with(binding) {

        val pref = requireContext().getSharedPreferences("ip_pref", MODE_PRIVATE)

        val ipShared = pref.getString("ip", "")
        if (!ipShared.isNullOrEmpty()) {
            ip.setText(ipShared)
        }

        button.setOnClickListener {
            val ipText = ip.text.toString().trim()
            if (ipText.isNotEmpty()) {
                pref.edit().apply {
                    putString("ip", ipText)
                    apply()
                }
                Toast.makeText(requireContext(), "Saved successfully", Toast.LENGTH_SHORT).show()

            //    runServer()
              //  runClient(ipText)

//                socketRunning = true
                CoroutineScope(Dispatchers.IO).launch {
                   // runClient(ipText)
                     runServer()
               }
            }
        }
    }

    private fun runServer() {
        try {
            val server = ServerSocket(4545)

            setLogText("Server running on port ${server.localPort}")
            val client = server.accept()
            setLogText("Client connected : ${client.inetAddress.hostAddress}")

            val clientOutputStream = client.getOutputStream()
            val clientInputStream = Scanner(client.getInputStream())

            thread {
                while (true) {

                    while (clientInputStream.hasNextLine()) {
                        setLogText(clientInputStream.nextLine())
                    }

//                    val nextByte = clientInputStream.read()
//                    setLogText(nextByte.toString())
                }
            }

            thread {
                while (true) {
                    //val input = readLine()
                    val input = "HELLO FROM SERVER!!"
                 //   client.outputStream.write(input.toByteArray())
                    clientOutputStream.write(input.toByteArray())
                }
            }

        } catch (e: Exception) {
            setLogText("Failed to connect!")
            setLogText(e.toString() ?: "")
            e.printStackTrace()
        }
    }

    private fun runClient(ip: String) {
        try {
            val client = Socket(ip, 4545)

            val clientOutputStream = client.getOutputStream()
            val clientInputStream = Scanner(client.getInputStream())

            thread {
//
//                val scanner = Scanner(client.inputStream)
//                while (scanner.hasNextLine()) {
//                    setLogText(scanner.nextLine())
//                    break
//                }

                while (clientInputStream.hasNextLine()) {
                    setLogText("${clientInputStream.nextLine()}")
                }
            }

            thread {
             //   while (true) {
                    //val input = readLine()
                    val input = "HELLO FROM CLIENT"
                    clientOutputStream.write(input.toByteArray())
                   // clientOutputStream.write(input.toByteArray())
             //   }
            }

        } catch (e: Exception) {
            setLogText("Connection failed")
            setLogText(e.toString() ?: "")
            e.printStackTrace()
        }
    }

    fun runServer2() {
        val server = ServerSocket(9999)
        setLogText("Server running on port ${server.localPort}")
        val client = server.accept()
        setLogText("Client connected : ${client.inetAddress.hostAddress}")
        val scanner = Scanner(client.inputStream)
        while (scanner.hasNextLine()) {
            setLogText(scanner.nextLine())
            break
        }
        server.close()
    }

    fun runClient2(ip: String) {
        val client = Socket(ip, 9999)
        client.outputStream.write("Hello from the client!".toByteArray())
        client.close()
    }

    private fun runServer(address: String, port: Int) {
        val connection = Socket(address, port)
//        val writer = connection.getOutputStream()
//        writer.write(1)
        setLogText("Server running on: ${connection.localAddress}, ${connection.localPort}")
        // val client = connection.accept()
        //  setLogText("Client connected : ${connection.localAddress}")
        val reader = Scanner(connection.getInputStream())
        while (socketRunning) {
            setLogText(reader.nextLine())
            //binding.textView.text = input
            //setTempHum()
        }
        reader.close()
        connection.close()
    }

    override fun setLogText(text: String): Unit = with(binding) {
        requireActivity().runOnUiThread {
            logsText.text = logsText.text.toString() + "\n" + text
        }
        Log.i("server", text)
    }


}