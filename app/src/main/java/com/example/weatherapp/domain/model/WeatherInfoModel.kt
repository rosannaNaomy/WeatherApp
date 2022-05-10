package com.example.weatherapp.domain.model

data class WeatherInfoModel(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String,
    val iconUrl: String = "https://openweathermap.org/img/wn/${icon}@2x.png"
)
