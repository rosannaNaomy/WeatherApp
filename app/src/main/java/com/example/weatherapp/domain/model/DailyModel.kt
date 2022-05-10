package com.example.weatherapp.domain.model

data class DailyModel(
    var temp: WeatherModel.TempModel,
    val dt: Int,
    val weather: List<WeatherInfoModel>
)