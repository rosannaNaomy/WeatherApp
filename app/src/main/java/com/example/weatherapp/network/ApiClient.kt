package com.example.weatherapp.network

import android.util.Log
import com.example.weatherapp.domain.mappers.WeatherMapper
import com.example.weatherapp.domain.model.WeatherModel
import com.example.weatherapp.network.response.Current
import com.example.weatherapp.network.response.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*
import kotlin.time.Duration.Companion.hours

class ApiClient(private val weatherService: WeatherService) {

    suspend fun getWeatherFromSearch(lat: Double, lon: Double): WeatherModel {
        val searchQuery = HashMap<String, String>() //Query map
        searchQuery["lat"] = lat.toString()
        searchQuery["lon"] = lon.toString()
        searchQuery["exclude"] = NetworkConstants.EXCLUDE
        searchQuery["units"] = NetworkConstants.UNITS
        searchQuery["appid"] = NetworkConstants.APIKEY

        val response = weatherService.getWeatherFromSearch(searchQuery)
        return WeatherMapper.buildFrom(weatherResponse = response.body()!!)
    }
}