package com.example.weatherapp.network.response


data class Daily(var temp: Temp,
                 val dt: Int,
                 val weather: List<Weather>)
