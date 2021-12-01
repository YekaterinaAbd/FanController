package com.example.fancontroller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.fancontroller.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpNavigation()
    }

    private fun setUpNavigation() {
        val navController: NavController =
            Navigation.findNavController(this, R.id.nav_host_fragment)

        NavigationUI.setupWithNavController(
            binding.bottomNavigationView,
            navController
        )
    }

}