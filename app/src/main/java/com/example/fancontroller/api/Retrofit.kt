package com.example.fancontroller.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Retrofit {

    fun getApi(): TempApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fancontroller-15882-default-rtdb.firebaseio.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createHttpClient())
            .build()
        return retrofit.create(TempApi::class.java)
    }

    private fun createHttpClient(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(createLoggingInterceptor())
        return okHttpClient.build()
    }

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(logger = object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("OkHttp", message)
            }
        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

}