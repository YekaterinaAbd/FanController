package com.example.fancontroller.ui

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.fancontroller.R

fun Context.getRed() = ContextCompat.getColor(this, R.color.red)

fun Context.getBeige() = ContextCompat.getColor(this, R.color.beige)

fun Context.getLightPurple() = ContextCompat.getColor(this, R.color.light_purple)

fun Context.getPurple() = ContextCompat.getColor(this, R.color.purple)

fun Context.getGrey() = ContextCompat.getColor(this, R.color.grey)

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}




