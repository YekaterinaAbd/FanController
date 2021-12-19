package com.example.fancontroller.ui

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.fancontroller.R
import com.example.fancontroller.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var animation: Animation

    private var currentIp: String? = null

    private var blowing: Boolean = false
    private var mode: Mode = Mode.AUTO
    private var cooling = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = requireContext().getPurple()
        observeViewModel()
        bindViews()
    }

    private fun observeViewModel() {
        sharedViewModel.message.observe(viewLifecycleOwner) { message ->
            when {
                message == "on" -> switch(true)
                message == "off" -> switch(false)
                message.startsWith("params") -> {
                    parseTempHum(message)
                }
            }
        }
    }

    private fun bindViews() = with(binding) {
        val pref = requireContext().getSharedPreferences("ip_pref", Context.MODE_PRIVATE)
        currentIp = pref.getString("ip", null)
        buttonTurn.setOnClickListener {
            buttonAuto.isEnabled = true
            if (mode == Mode.AUTO) {
                mode = Mode.MANUAL
                setButtonBackground()
                (requireActivity() as MainActivity).post("manual")
            } else {
                (requireActivity() as MainActivity).post("switch")
                blowing = !blowing
                switch(blowing)
            }
        }
        buttonAuto.setOnClickListener {
            mode = Mode.AUTO
            setButtonBackground()
            (requireActivity() as MainActivity).post("auto")
            it.isEnabled = false
        }
    }

    private fun setButtonBackground() = with(binding) {
        if (mode == Mode.MANUAL) {
            buttonTurn.backgroundTintList =
                ColorStateList.valueOf(requireContext().getLightPurple())
            buttonAuto.backgroundTintList = ColorStateList.valueOf(requireContext().getGrey())
            buttonTurn.text = getString(R.string.turn_on)
        } else {
            buttonAuto.backgroundTintList =
                ColorStateList.valueOf(requireContext().getLightPurple())
            buttonTurn.backgroundTintList = ColorStateList.valueOf(requireContext().getGrey())
            buttonTurn.text = getString(R.string.switch_mode)
        }
    }

    private fun switch(blowing: Boolean) {
        when (blowing) {
            true -> turnOn()
            false -> turnOff()
        }
        this.blowing = blowing
    }

    private fun turnOn() = with(binding) {
        ImageViewCompat.setImageTintList(fan, ColorStateList.valueOf(requireContext().getBeige()))
        buttonTurn.backgroundTintList = ColorStateList.valueOf(requireContext().getBeige())
        buttonTurn.text = getString(R.string.turn_off)
        status.text = getString(if (cooling) R.string.cooling else R.string.heating)
        startFanAnimation()
    }

    private fun turnOff() = with(binding) {
        ImageViewCompat.setImageTintList(fan, ColorStateList.valueOf(requireContext().getGrey()))
        buttonTurn.backgroundTintList = ColorStateList.valueOf(requireContext().getLightPurple())
        buttonTurn.text = getString(R.string.turn_on)
        status.text = getString(R.string.resting)
        stopFanAnimation()
    }

    private fun parseTempHum(string: String) {
        var firstPos = 0
        var secondPos = 0
        for (i in string.indices) {
            if (string[i] == ' ') {
                if (firstPos == 0) firstPos = i + 1
                else secondPos = i + 1
            }
        }
        val temp = string.substring(firstPos, secondPos - 1)
        val hum = string.substring(secondPos)
        setTempHum(temp, hum)
    }

    private fun setTempHum(temp: String, hum: String) = with(binding) {
        temperature.text = getString(R.string.temperature_value, temp)
        humidity.text = getString(R.string.humidity_value, hum) + " %"
        temperatureBig.text = getString(R.string.temp_big_value, temp)
        circularProgressIndicator.setProgressCompat(100 * temp.toInt() / 50, true)
        cooling = temp.toInt() >= 20

        (requireActivity() as MainActivity).updateAverageParams(temp, hum)
    }

    private fun startFanAnimation() {
        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_center)
        binding.fan.startAnimation(animation)
    }

    private fun stopFanAnimation() {
        binding.fan.clearAnimation()
        animation.cancel()
    }

    enum class Mode {
        AUTO, MANUAL
    }

}