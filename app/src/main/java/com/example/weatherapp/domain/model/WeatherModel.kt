package com.example.weatherapp.domain.model

import com.example.weatherapp.network.response.Current
import com.example.weatherapp.ui.UiConstants

data class WeatherModel(
    val currentModel: CurrentModel,
    val daily: List<DailyModel>,
    val lat: Double,
    val lon: Double,
    val timezone: String
) {
    data class CurrentModel(
        val temp: Double,
        val dt: Int,
        val weather: List<WeatherInfoModel>,
        var toggleId: Int = UiConstants.FAHRENHEIT_ID,
        var celsius: Double = (temp - 32.0) * 5.0 / 9.0
    )

    data class TempModel(
        var day: Double,
        var toggleId: Int = UiConstants.FAHRENHEIT_ID, //2131362283,
        var celsius: Double = (day - 32.0) * 5.0 / 9.0
    )

}
