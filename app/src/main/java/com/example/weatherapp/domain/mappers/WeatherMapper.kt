package com.example.weatherapp.domain.mappers

import android.location.Geocoder
import com.example.weatherapp.domain.model.WeatherModel
import com.example.weatherapp.network.response.WeatherResponse
import com.example.weatherapp.ui.UiConstants
import java.util.*


object WeatherMapper {

    fun buildFrom(weatherResponse: WeatherResponse): WeatherModel {
        return WeatherModel(
            currentModel = WeatherModel.CurrentModel(
                temp = weatherResponse.current.temp,
                dt = weatherResponse.current.dt,
                date = getDate(weatherResponse.current.dt),
                celsius = getCelsius(weatherResponse.current.temp),
                unitId = setUnitId(),
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

    fun getDate(dt: Int): String {
        val date = Date(dt * 1000L)
        val c: Calendar = Calendar.getInstance()
        c.time = date

        val day = c.get(Calendar.DATE)
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)
        val displayMin = if (minute < 10) "0$minute" else minute
        val am_pm = if (c.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
        val dayOfWeek = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US)
        val monthName = c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)

        return "$dayOfWeek $monthName, $day $hour:$displayMin $am_pm"
    }

    fun getCelsius(temp: Double): Double{
        return (temp - 32.0) * 5.0 / 9.0
    }

    fun setUnitId(): Int{
        return UiConstants.FAHRENHEIT_ID
    }
}