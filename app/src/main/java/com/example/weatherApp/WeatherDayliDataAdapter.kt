package com.example.weatherApp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherApp.dataBase.FiveDayWeatherList
import com.example.weatherApp.helper.getWeatherImageId
import com.squareup.picasso.Picasso


class WeatherDayliDataAdapter(var context: Context, var weathers: List<FiveDayWeatherList>) :
    RecyclerView.Adapter<WeatherDayliDataAdapter.ViewHolder>() {
    var inflater = LayoutInflater.from(context);

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.weather_dayli_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = weathers.get(position)
//        val imageUrl = "http://openweathermap.org/img/wn/".plus(weather.icon.plus("@2x.png"))
//        Picasso.with(context).load(imageUrl).into(holder.imageView)
        holder.imageView.setImageResource(getWeatherImageId(weather.icon))
        holder.weekdayTV.text = weather.getWeatherTime()
        holder.temperatureTV.text = weather.temp.toString()
    }

    override fun getItemCount(): Int {
        return weathers.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal val imageView: ImageView
        internal val weekdayTV: TextView
        internal val temperatureTV: TextView

        init {
            imageView = view.findViewById(R.id.imageWeather) as ImageView
            weekdayTV = view.findViewById(R.id.weekdayTV)
            temperatureTV = view.findViewById(R.id.temperatureTV)
        }
    }
}