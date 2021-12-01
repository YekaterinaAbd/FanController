package com.example.fancontroller

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class StatsFragment: Fragment(R.layout.fragment_stats) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = requireContext().getLightPurple()
    }
}