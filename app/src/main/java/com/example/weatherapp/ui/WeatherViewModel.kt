package com.example.weatherapp.ui

import android.Manifest
import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import com.example.weatherapp.MainActivity
import com.example.weatherapp.domain.mappers.WeatherMapper
import com.example.weatherapp.domain.model.DailyModel
import com.example.weatherapp.domain.model.WeatherModel
import com.example.weatherapp.network.NetworkConstants
import com.example.weatherapp.network.NetworkLayer
import com.example.weatherapp.network.response.Current
import com.example.weatherapp.network.response.Daily
import com.example.weatherapp.network.response.WeatherResponse
import kotlinx.coroutines.launch
import retrofit2.http.GET
import java.util.*
import kotlin.collections.HashMap

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private var dailyList = listOf<DailyModel>()
    private lateinit var currentDay: WeatherModel.CurrentModel

    private val _daily = MutableLiveData<List<DailyModel>>()
    val daily: MutableLiveData<List<DailyModel>>
        get() = _daily

    private val _current = MutableLiveData<WeatherModel.CurrentModel>()
    val current: MutableLiveData<WeatherModel.CurrentModel>
        get() = _current

    private val _locationName = MutableLiveData<String>()
    val locationName: MutableLiveData<String>
        get() = _locationName

    fun toggle(checkedId: Int) {
        val newList = dailyList.map { it.apply { it.temp.toggleId = checkedId } }
        val newDay = currentDay.apply { toggleId = checkedId }
        newDay.let { _current.postValue(it) }
        newList.let { _daily.postValue(it) }
    }

    fun getFahrenheitCelsius(current: WeatherModel.CurrentModel): String {
        val fahrenheit = "${current.temp.toInt()}\u2109"
        val celsius = "${current.celsius.toInt()}\u2103"
        return if (current.toggleId == UiConstants.FAHRENHEIT_ID) fahrenheit else celsius
    }

    fun getLatLon(search: String) {
        val gc = Geocoder(getApplication(), Locale.US)
        val addresses = gc.getFromLocationName(search, 1)
        if (addresses.size > 0) {
            val lat = addresses[0].latitude
            val lon = addresses[0].longitude
            getWeatherFromSearch(lat,lon)
        } else {
            Toast.makeText(getApplication(), "not found", Toast.LENGTH_SHORT).show()
        }
    }

    fun getWeatherFromSearch(lat:Double, lon:Double) {
        viewModelScope.launch {
            try {
                val response = NetworkLayer.apiClient.getWeatherFromSearch(lat,lon)
                dailyList = response.daily
                currentDay = response.currentModel
                _current.value = response.currentModel
                _daily.value = response.daily
                _locationName.value = getLocationName(response)
            } catch (e: Exception) {
                Log.e(WeatherViewModel::class.simpleName, "/call failed: ${e.javaClass}", e)
            }
        }
    }

    private fun getLocationName(weatherModel: WeatherModel): String{
        val gc = Geocoder(getApplication(), Locale.US)
        val lat = weatherModel.lat
        val lon = weatherModel.lon

        val location = gc.getFromLocation(lat, lon, 1)
        val locationName = location[0].adminArea

        return locationName
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
}