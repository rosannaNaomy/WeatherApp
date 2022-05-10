package com.example.weatherapp.ui

import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.WeatherItemBinding
import com.example.weatherapp.domain.model.DailyModel
import com.example.weatherapp.network.response.Daily
import com.squareup.picasso.Picasso
import java.util.*

class WeatherAdapter(): RecyclerView.Adapter<WeatherAdapter.MyViewHolder>() {

    private var listData = mutableListOf<DailyModel>()

    fun updateList(daily: List<DailyModel>) {
        this.listData.clear()
        this.listData.addAll(daily)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = WeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherAdapter.MyViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    class MyViewHolder(weatherItemBinding: WeatherItemBinding) : RecyclerView.ViewHolder(weatherItemBinding.root) {
        val tempText = weatherItemBinding.tempTextView
        val dayTV = weatherItemBinding.dayOfWeekTextview
        val icon = weatherItemBinding.weatherIcon
        val getResources = weatherItemBinding.root.resources

        fun bind(daily: DailyModel) {
            val fahrenheitId = UiConstants.FAHRENHEIT_ID//2131362283
            val celsiusId = UiConstants.CELSIUS_ID //2131362282
            val fahrenheit = "${daily.temp.day.toInt()}\u2109"
            val celsius = "${daily.temp.celsius.toInt()}\u2103"
            val iconPath: String = daily.weather[0].iconUrl

            val date = Date(daily.dt * 1000L)
            val c: Calendar = Calendar.getInstance()
            c.setTime(date)
            val dateLocal = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US)
            val day = c.get(Calendar.DATE)
            val month = c.get(Calendar.MONTH) + 1
            dayTV.text = getResources.getString(R.string.weather_item_date_text, dateLocal, month, day)

            when(daily.temp.toggleId){
                fahrenheitId -> tempText.text = fahrenheit
                celsiusId -> tempText.text = celsius
            }
            Picasso.get().load(iconPath).into(icon)
        }
    }
}