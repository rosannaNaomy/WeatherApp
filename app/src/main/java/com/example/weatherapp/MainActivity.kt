package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.domain.model.WeatherModel
import com.example.weatherapp.ui.UiConstants
import com.example.weatherapp.ui.WeatherAdapter
import com.example.weatherapp.ui.WeatherViewModel
import com.google.android.gms.location.*
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerViewAdapter: WeatherAdapter
    private lateinit var weatherViewModel: WeatherViewModel

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        getCurrentLocation()
        weatherUpdates()
        setUpRV()
        viewEvents()
    }


    //<------> Permissions check and Location requests <------->

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnable()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        getNewLocation()
                    } else {
                        val lat = location.latitude
                        val lon = location.longitude
                        weatherViewModel.getWeather(lat,lon)
                    }
                }
            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        val locationRequest = LocationRequest.create()
            .setInterval(100)
            .setFastestInterval(3000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setNumUpdates(2)
            .setMaxWaitTime(100)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()!!
        )
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            weatherViewModel.getWeather(lastLocation.latitude, lastLocation.longitude)
        }
    }

    private fun isLocationEnable(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            UiConstants.PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == UiConstants.PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }


    //<------> View events and set up <------->

    private fun viewEvents() {
        binding.toggleGroup.addOnButtonCheckedListener { toggleGroup, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.toggle_fah_button -> weatherViewModel.toggleFC(UiConstants.FAHRENHEIT_ID)
                    R.id.toggle_cel_button -> weatherViewModel.toggleFC(UiConstants.CELSIUS_ID)
                }
            }
        }
        binding.searchWeatherButton.setOnClickListener {
            val search = binding.weatherSearchEditTextView.text.toString()
            weatherViewModel.getLatLon(search)
            binding.weatherSearchEditTextView.setText("")
            binding.mylocationButton.visibility = View.VISIBLE
        }
        binding.weatherSearchEditTextView.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val search = binding.weatherSearchEditTextView.text.toString()
                weatherViewModel.getLatLon(search)
                binding.weatherSearchEditTextView.setText("")
                clearFocus()
                binding.mylocationButton.visibility = View.VISIBLE
                return@OnKeyListener true
            }
            false
        })

        binding.mylocationButton.setOnClickListener {
            getCurrentLocation()
            binding.mylocationButton.visibility = View.INVISIBLE
        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        clearFocus()
        return super.dispatchTouchEvent(ev)
    }

    private fun clearFocus() {
        currentFocus?.apply {
            if (this is EditText) {
                clearFocus()
            }
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun setUpHeader(current: WeatherModel.CurrentModel) {
        weatherViewModel.locationName.observe(this) {
            binding.currentLocationNameTextview.text = it
        }
        binding.todayTextview.visibility = View.VISIBLE
        binding.currentDateTextview.text = current.date
        binding.currentTempTextView.text = weatherViewModel.getFahrenheitCelsius()
        val iconPath = current.weather[0].iconUrl
        Picasso.get().load(iconPath).into(binding.currentWeatherIcon)
    }

    private fun weatherUpdates() {
        weatherViewModel.daily.observe(this) {
            recyclerViewAdapter.updateList(it)
        }
        weatherViewModel.current.observe(this) {
            setUpHeader(it)
        }
    }

    private fun setUpRV() {
        binding.recyclerDailyWeather.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            val decoration =
                DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            recyclerViewAdapter = WeatherAdapter()
            adapter = recyclerViewAdapter
        }
    }
}