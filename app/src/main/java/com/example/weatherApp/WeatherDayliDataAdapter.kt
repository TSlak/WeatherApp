package com.example.weatherApp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherApp.dataBase.DailyWeather
import com.example.weatherApp.dataBase.HourlyWeather
import com.example.weatherApp.helper.getWeatherImageId
import java.text.SimpleDateFormat
import java.util.*


class WeatherDayliDataAdapter(context: Context, var dailyWeatherList: List<DailyWeather>) :
    RecyclerView.Adapter<WeatherDayliDataAdapter.ViewHolder>() {
    var inflater = LayoutInflater.from(context)
    val sdf = SimpleDateFormat("dd:MM", Locale.ROOT)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.weather_dayli_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dailyWeather = dailyWeatherList[position]
        holder.imageView.setImageResource(getWeatherImageId(dailyWeather.hourlyWeatherList.first()!!.icon))
        val hourlyWeatherTemp = dailyWeather.hourlyWeatherList
            .map(HourlyWeather::temp)
        holder.weekdayTV.text = sdf.format(dailyWeather.date)
        holder.temperatureTV.text = hourlyWeatherTemp.max()
            .toString()
            .plus(" ℃")
            .plus("\n")
            .plus(hourlyWeatherTemp.min())
            .plus(" ℃")
    }

    override fun getItemCount(): Int {
        return dailyWeatherList.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal val imageView: ImageView = view.findViewById(R.id.imageWeather) as ImageView
        internal val weekdayTV: TextView = view.findViewById(R.id.weekdayTV)
        internal val temperatureTV: TextView = view.findViewById(R.id.temperatureTV)
    }
}