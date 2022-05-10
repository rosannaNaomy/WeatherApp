package com.example.weatherapp.network.response


data class Current(val temp: Double,
                   val dt: Int,
                   val weather: List<Weather>)
