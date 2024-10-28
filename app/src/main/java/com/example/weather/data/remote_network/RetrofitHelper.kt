package com.example.weather.data.remote_network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    object static{
        val url:String= "https://api.openweathermap.org/data/2.5/"
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging) // Add logging interceptor
        .build()
    val retofiConntector: Retrofit = Retrofit.Builder()
        .baseUrl(static.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
object API{
    val retrofitService:WeatherService by lazy {
        RetrofitHelper.retofiConntector.create(WeatherService::class.java)
    }
}