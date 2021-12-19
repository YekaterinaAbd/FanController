package com.example.fancontroller.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.fancontroller.R
import com.example.fancontroller.databinding.ActivityMainBinding
import com.example.fancontroller.model.Humidity
import com.example.fancontroller.model.Temperature
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.net.InetAddress
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    companion object {
        var lastTemp: Temperature? = null
        var lastHum: Humidity? = null
    }

    var port = 3003
    var ipAddress: String = "192.168.0.107"
    val delay: Long = 7_000

    private val sharedViewModel: SharedViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private var clientThread: ClientThread? = null
    private var thread: Thread? = null
    private var handler: Handler? = null

    private lateinit var request: Request
    private val client = OkHttpClient()

    private val binding by viewBinding(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handler = Handler(Looper.getMainLooper())

        setUpNavigation()
        loopUpdateData()

        mainViewModel.getTemperatureList()
        mainViewModel.getHumidityList()

    }

    private fun setUpNavigation() {
        val navController: NavController =
            Navigation.findNavController(this, R.id.nav_host_fragment)

        NavigationUI.setupWithNavController(
            binding.bottomNavigationView,
            navController
        )
    }

    fun post(post: String) {
        Thread {
            request = Request.Builder().url("http://${ipAddress}/$post").build()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val resultText = response.body?.string()
                    if (resultText != null) {
                        setMessage(resultText)
                    }
                }
            } catch (i: IOException) {

            }

        }.start()
    }

    private fun loopUpdateData() {
        val delayHandler = Handler(Looper.getMainLooper())

        delayHandler.postDelayed(object : Runnable {
            override fun run() {
                post("params")
                delayHandler.postDelayed(this, delay)
            }
        }, delay)
    }

    fun updateAverageParams(temp: String, hum: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val previousTempDate = lastTemp?.date ?: ""
        val previousHumDate = lastHum?.date ?: ""

        if (currentDate != previousTempDate) mainViewModel.addTemperature(temp, currentDate)
        else if (currentDate == previousTempDate) mainViewModel.updateTemperature(temp)

        if (currentDate != previousHumDate) mainViewModel.addHumidity(hum, currentDate)
        else if (currentDate == previousHumDate) mainViewModel.updateHumidity(hum)
    }

    fun setUpClient() {
        clientThread = ClientThread()
        thread = Thread(clientThread)
        thread!!.start()
    }

    fun sendClientMessage(message: String) {
        val request = "http://$ipAddress/$message"
        clientThread?.sendMessage(request)
        clientThread?.sendMessage(message)
    }

    inner class ClientThread : Runnable {
        private var socket: Socket? = null
        private var input: BufferedReader? = null

        override fun run() {
            try {
                val serverAddr = InetAddress.getByName(ipAddress)
                socket = Socket(serverAddr, port)

                while (!Thread.currentThread().isInterrupted) {
                    input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                    val message = input!!.readLine()
                    if (message == null || "Disconnect".contentEquals(message)) {
                        Thread.interrupted()
                        setMessage("Server Disconnected")
                        break
                    }
                    setMessage("Server: $message")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun sendMessage(message: String?) {
            Thread {
                try {
                    if (socket != null) {
                        val out =
                            PrintWriter(BufferedWriter(OutputStreamWriter(socket!!.getOutputStream())),
                                true)
                        out.println(message)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }

    }

    fun setMessage(message: String) {
        handler?.post {
            sharedViewModel.message.value = message
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (clientThread != null) {
            clientThread!!.sendMessage("Disconnect")
            clientThread = null
        }

    }

}
