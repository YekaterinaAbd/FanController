package com.example.fancontroller.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    val message = MutableLiveData<String>()

}