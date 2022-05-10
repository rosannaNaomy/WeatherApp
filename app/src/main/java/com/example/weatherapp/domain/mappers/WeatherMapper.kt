package com.example.weatherapp.domain.mappers

import com.example.weatherapp.domain.model.WeatherModel
import com.example.weatherapp.network.response.WeatherResponse


object WeatherMapper {

    fun buildFrom(weatherResponse: WeatherResponse): WeatherModel {
        return WeatherModel(
            currentModel = WeatherModel.CurrentModel(
                temp = weatherResponse.current.temp,
                dt = weatherResponse.current.dt,
                weather = weatherResponse.current.weather.map {
                    WeatherInfoMapper.buildFrom(it)
                }
            ),
            daily = weatherResponse.daily.map {
                DailyMapper.buildFrom(it)
            },
            lat = weatherResponse.lat,
            lon = weatherResponse.lon,
            timezone = weatherResponse.timezone
        )
    }

}