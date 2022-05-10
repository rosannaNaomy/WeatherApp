package com.example.weatherapp.domain.mappers

import com.example.weatherapp.domain.model.DailyModel
import com.example.weatherapp.domain.model.WeatherModel
import com.example.weatherapp.network.response.Daily
import com.example.weatherapp.network.response.WeatherResponse

object DailyMapper {

    fun buildFrom(daily: Daily): DailyModel {
        return DailyModel(
            temp = WeatherModel.TempModel(
                day = daily.temp.day
            ),
            dt = daily.dt,
            weather = daily.weather.map {
                WeatherInfoMapper.buildFrom(it)
            }
        )
    }

}