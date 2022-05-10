package com.example.weatherapp.network

import com.example.weatherapp.network.response.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface WeatherService {

    @GET("onecall?")
    suspend fun getWeatherFromSearch(@QueryMap searchQuery: HashMap<String,String>): Response<WeatherResponse>

}