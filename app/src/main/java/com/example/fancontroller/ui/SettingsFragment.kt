package com.example.fancontroller.ui

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.fancontroller.R
import com.example.fancontroller.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel: SharedViewModel by activityViewModels()

    private var handler: Handler? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = requireContext().getLightPurple()
        handler = Handler(Looper.getMainLooper())
        observeViewModel()
        bindViews()
    }

    private fun bindViews() = with(binding) {

        val pref = requireContext().getSharedPreferences("ip_pref", MODE_PRIVATE)

        val ipShared = pref.getString("ip", "")
        if (!ipShared.isNullOrEmpty()) {
            ip.setText(ipShared)
        }
        port.setText((requireActivity() as MainActivity).port.toString())

        buttonConnect.setOnClickListener {
            val ipText = ip.text.toString().trim()
            (requireActivity() as MainActivity).ipAddress = ipText

            val portText = port.text.toString().trim()
            (requireActivity() as MainActivity).port = portText.toInt()

            if (ipText.isNotEmpty()) {
                pref.edit().apply {
                    putString("ip", ipText)
                    apply()
                }

                logsText.text = ""
            }
        }

        buttonSend.setOnClickListener {
            val message = input.text.toString().trim()
            if (message.isNotEmpty()) {
                showMessage(message)
                (requireActivity() as MainActivity).post(message)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.message.observe(viewLifecycleOwner) { message ->
            binding.logsText.append("\n" + message)
        }
    }

    private fun showMessage(message: String) {
        handler!!.post { binding.logsText.append("\n" + message) }
    }

}