package com.example.fancontroller.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fancontroller.api.Retrofit
import com.example.fancontroller.model.Humidity
import com.example.fancontroller.model.Temperature
import com.google.gson.internal.LinkedTreeMap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    var temperatureLD: MutableLiveData<List<Temperature>> = MutableLiveData()
    var humidityLD: MutableLiveData<List<Humidity>> = MutableLiveData()

    private val retrofit = Retrofit.getApi()

    fun getTemperatureList() {
        retrofit.getTemperatureList().enqueueTemperatureResponse()
    }

    fun getHumidityList() {
        retrofit.getHumidityList().enqueueHumidityResponse()
    }

    fun addTemperature(temp: String, date: String) {
        val temperature = Temperature("xxx", temp, date)
        retrofit.addTemperature(temperature).enqueueResponse()
    }

    fun addHumidity(hum: String, date: String) {
        val humidity = Humidity("xxx", hum, date)
        retrofit.addHumidity(humidity).enqueueResponse()
    }

    fun updateTemperature(newTemp: String) {
        val oldTemp = MainActivity.lastTemp ?: return
        val temp = ((oldTemp.temp).toInt() + newTemp.toInt()) / 2
        val temperature = Temperature("0", temp.toString(), oldTemp.date)
        retrofit.updateTemperature(oldTemp.id, temperature).enqueueResponse()
    }

    fun updateHumidity(newHum: String) {
        val oldHum = MainActivity.lastHum ?: return
        val hum = ((oldHum.hum).toInt() + newHum.toInt()) / 2
        val temperature = Temperature("0", hum.toString(), oldHum.date)
        retrofit.updateTemperature(oldHum.id, temperature).enqueueResponse()
    }

    private fun Call<Any>.enqueueTemperatureResponse() {
        this.enqueue(object : Callback<Any> {
            override fun onResponse(
                call: Call<Any>, response: Response<Any>,
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {

                        val temperatures = mutableListOf<Temperature>()

                        val answer: Map<String, LinkedTreeMap<String, String>> =
                            response.body() as Map<String, LinkedTreeMap<String, String>>
                        val keys: Set<String> = answer.keys
                        for (key in keys) {
                            val value = answer[key]
                            val temp = Temperature(
                                date = value?.get("date") ?: "",
                                id = key,
                                temp = value?.get("temp") ?: ""
                            )
                            temperatures.add(temp)
                        }
                        temperatureLD.value = temperatures
                        MainActivity.lastTemp = temperatures.last()
                    }
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                // view.showError(t)
            }
        })
    }

    private fun Call<Any>.enqueueHumidityResponse() {
        this.enqueue(object : Callback<Any> {
            override fun onResponse(
                call: Call<Any>, response: Response<Any>,
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {

                        val humidities = mutableListOf<Humidity>()

                        val answer: Map<String, LinkedTreeMap<String, String>> =
                            response.body() as Map<String, LinkedTreeMap<String, String>>
                        val keys: Set<String> = answer.keys
                        for (key in keys) {
                            val value = answer[key]
                            val hum = Humidity(
                                date = value?.get("date") ?: "",
                                id = key,
                                hum = value?.get("hum") ?: ""
                            )
                            humidities.add(hum)
                        }
                        humidityLD.value = humidities
                        MainActivity.lastHum = humidities.last()
                    }
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                // view.showError(t)
            }
        })
    }

    private fun Call<Any>.enqueueResponse() {
        this.enqueue(object : Callback<Any> {
            override fun onResponse(
                call: Call<Any>, response: Response<Any>,
            ) {
                if (response.isSuccessful) Log.d("testt", response.body().toString())
                else Log.d("testt", "error")
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}