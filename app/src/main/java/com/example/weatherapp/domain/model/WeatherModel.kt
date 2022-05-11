package com.example.weatherapp.domain.model

import com.example.weatherapp.network.response.Current
import com.example.weatherapp.ui.UiConstants

data class WeatherModel(
    val currentModel: CurrentModel,
    val daily: List<DailyModel>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
) {
    data class CurrentModel(
        val temp: Double,
        val dt: Int,
        val weather: List<WeatherInfoModel>,
        var date: String,
        var unitId: Int,
        var celsius: Double
    )

    data class TempModel(
        var day: Double,
        var unitId: Int,
        var celsius: Double
    )

}
