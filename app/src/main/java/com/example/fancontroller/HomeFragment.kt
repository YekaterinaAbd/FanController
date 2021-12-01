package com.example.fancontroller

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.fancontroller.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.Socket
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private lateinit var animation: Animation

    private lateinit var request: Request
    private val client = OkHttpClient()

    private var currentIp: String? = null

    private var blowing: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = requireContext().getPurple()
        bindViews()
    }

    private fun bindViews() = with(binding) {
        val pref = requireContext().getSharedPreferences("ip_pref", Context.MODE_PRIVATE)
        currentIp = pref.getString("ip", null)
        button.setOnClickListener {
          //  sendCall()
            switch()
        }
    }


    private fun sendCall() {
        if (currentIp.isNullOrEmpty()) {
            requireContext().showToast("Please provide an IP Address")
            return
        }
        Thread {
            request = Request.Builder().url("http://${currentIp}/switch").build()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    switch()
//                    val resultText = response.body?.string()
//                    requireActivity().runOnUiThread {
//                        val temp = resultText + "Cº"
//                        binding.temperature.text = temp
//                    }
                }
            } catch (i: IOException) {
                i.printStackTrace()
            }

        }.start()
    }

    private fun switch() {
        when (blowing) {
            true -> turnOff()
            false -> turnOn()
        }
        blowing = !blowing
    }

    private fun turnOn() = with(binding) {
        ImageViewCompat.setImageTintList(fan, ColorStateList.valueOf(requireContext().getBeige()))
        button.backgroundTintList = ColorStateList.valueOf(requireContext().getBeige())
        button.text = getString(R.string.turn_off)
        status.text = getString(R.string.cooling)
        startFanAnimation()
    }

    private fun turnOff() = with(binding) {
        ImageViewCompat.setImageTintList(fan, ColorStateList.valueOf(requireContext().getGrey()))
        button.backgroundTintList = ColorStateList.valueOf(requireContext().getLightPurple())
        button.text = getString(R.string.turn_on)
        status.text = getString(R.string.resting)
        stopFanAnimation()
    }

    private fun setTempHum(temp: Int, hum: Int) = with(binding) {
        temperature.text = getString(R.string.temperature_value, temp)
        humidity.text = getString(R.string.humidity_value, hum)
        temperatureBig.text = getString(R.string.temperature_value, temp)
        circularProgressIndicator.setProgressCompat(100 * temp / 50, true)
    }

    private fun startFanAnimation() {
        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_center)
        binding.fan.startAnimation(animation)
    }

    private fun stopFanAnimation() {
        binding.fan.clearAnimation()
        animation.cancel()
    }

    private fun updateProgress(value: Int) {
        binding.circularProgressIndicator.setProgressCompat(value, true)
    }

}