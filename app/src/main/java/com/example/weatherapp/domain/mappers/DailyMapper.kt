package com.example.weatherapp.domain.mappers

import com.example.weatherapp.R
import com.example.weatherapp.domain.model.DailyModel
import com.example.weatherapp.domain.model.WeatherModel
import com.example.weatherapp.network.response.Daily
import com.example.weatherapp.network.response.WeatherResponse
import com.example.weatherapp.ui.UiConstants
import java.util.*

object DailyMapper {

    fun buildFrom(daily: Daily): DailyModel {
        return DailyModel(
            temp = WeatherModel.TempModel(
                day = daily.temp.day,
                celsius = getCelsius(daily.temp.day),
                unitId = setUnitId()
            ),
            dt = daily.dt,
            weather = daily.weather.map {
                WeatherInfoMapper.buildFrom(it)
            },
            date = getDate(daily.dt)
        )
    }

    fun getDate(dt: Int): String {
        val date = Date(dt * 1000L)
        val c: Calendar = Calendar.getInstance()
        c.setTime(date)
        val dateLocal = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US)
        val day = c.get(Calendar.DATE)
        val month = c.get(Calendar.MONTH) + 1

        return "$dateLocal $month|$day"
    }

    fun getCelsius(temp: Double): Double{
        return (temp - 32.0) * 5.0 / 9.0
    }

    fun setUnitId(): Int{
        return UiConstants.FAHRENHEIT_ID
    }

}