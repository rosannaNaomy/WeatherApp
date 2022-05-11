package com.example.weatherapp.ui

import android.app.Application
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.weatherapp.domain.model.DailyModel
import com.example.weatherapp.domain.model.WeatherModel
import com.example.weatherapp.network.NetworkLayer
import kotlinx.coroutines.launch
import java.util.*

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

    fun toggleFC(checkedId: Int) {
        val newList = dailyList.map { it.apply { it.temp.unitId = checkedId } }
        val newDay = currentDay.apply { unitId = checkedId }
        newDay.let { _current.postValue(it) }
        newList.let { _daily.postValue(it) }
    }

    fun getFahrenheitCelsius(): String {
        val fahrenheit = "${currentDay.temp.toInt()}\u2109"
        val celsius = "${currentDay.celsius.toInt()}\u2103"
        return if (currentDay.unitId == UiConstants.FAHRENHEIT_ID) fahrenheit else celsius
    }

    fun getLatLon(search: String) {
        val gc = Geocoder(getApplication(), Locale.US)
        val addresses = gc.getFromLocationName(search, 1)
        if (addresses.size > 0) {
            val lat = addresses[0].latitude
            val lon = addresses[0].longitude
            getWeather(lat, lon)
        } else {
            Toast.makeText(getApplication(), "not found", Toast.LENGTH_SHORT).show()
        }
    }

    fun getWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = NetworkLayer.apiClient.getWeatherFromSearch(lat, lon)
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

    private fun getLocationName(weatherModel: WeatherModel): String {
        val gc = Geocoder(getApplication(), Locale.US)
        val lat = weatherModel.lat
        val lon = weatherModel.lon

        val location = gc.getFromLocation(lat, lon, 1)
        val locationName = location[0].adminArea

        return locationName
    }


}