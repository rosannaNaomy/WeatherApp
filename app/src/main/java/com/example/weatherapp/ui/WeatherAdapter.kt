package com.example.weatherapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.WeatherItemBinding
import com.example.weatherapp.domain.model.DailyModel
import com.squareup.picasso.Picasso

class WeatherAdapter() : RecyclerView.Adapter<WeatherAdapter.MyViewHolder>() {

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

    class MyViewHolder(weatherItemBinding: WeatherItemBinding) :
        RecyclerView.ViewHolder(weatherItemBinding.root) {
        val tempText = weatherItemBinding.tempTextView
        val dayTV = weatherItemBinding.dayOfWeekTextview
        val icon = weatherItemBinding.weatherIcon

        fun bind(daily: DailyModel) {
            val fahrenheit = "${daily.temp.day.toInt()}\u2109"
            val celsius = "${daily.temp.celsius.toInt()}\u2103"
            val iconPath: String = daily.weather[0].iconUrl

            dayTV.text = daily.date

            if (daily.temp.unitId ==
                UiConstants.FAHRENHEIT_ID)
                    tempText.text = fahrenheit else tempText.text = celsius

            Picasso.get().load(iconPath).into(icon)
        }
    }
}