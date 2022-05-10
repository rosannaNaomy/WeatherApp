package com.example.weatherapp.domain.mappers

import com.example.weatherapp.domain.model.WeatherInfoModel
import com.example.weatherapp.network.response.Weather

object WeatherInfoMapper {

    fun buildFrom(weather: Weather): WeatherInfoModel {
        return WeatherInfoModel(
            description = weather.description,
            icon = weather.icon,
            id = weather.id,
            main = weather.main
        )
    }
}