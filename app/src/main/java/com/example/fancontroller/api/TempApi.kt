package com.example.fancontroller.api

import com.example.fancontroller.model.Humidity
import com.example.fancontroller.model.Temperature
import retrofit2.Call
import retrofit2.http.*

interface TempApi {

    @GET("temp.json")
    fun getTemperatureList(): Call<Any>

    @PATCH("temp/{id}/.json")
    fun updateTemperature(
        @Path("id") id: String,
        @Body temp: Temperature,
    ): Call<Any>

    @POST("temp.json")
    fun addTemperature(
        @Body temp: Temperature,
    ): Call<Any>

    @GET("hum.json")
    fun getHumidityList(): Call<Any>

    @PATCH("hum/{id}/.json")
    fun updateHumidity(
        @Path("id") id: String,
        @Body hum: Humidity,
    ): Call<Any>

    @POST("hum.json")
    fun addHumidity(
        @Body hum: Humidity,
    ): Call<Any>
}