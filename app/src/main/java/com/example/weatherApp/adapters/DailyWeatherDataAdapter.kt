package com.example.weatherApp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherApp.R
import com.example.weatherApp.dataBase.ForecastForDay
import com.example.weatherApp.dataBase.HourlyWeather
import com.example.weatherApp.helper.IconHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class DailyWeatherDataAdapter(context: Context, var forecastForDayList: List<ForecastForDay>) :
    RecyclerView.Adapter<DailyWeatherDataAdapter.ViewHolder>() {
    private var clickListener: ClickListener? = null
    var inflater = LayoutInflater.from(context)
    val sdf = SimpleDateFormat("dd.MM", Locale.ROOT)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.weather_dayli_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dailyWeather = forecastForDayList[position]
        holder.imageView.setImageResource(IconHelper.getWeatherImageId(dailyWeather.hourlyWeather.first()!!.icon))
        val hourlyWeatherTemp = dailyWeather.hourlyWeather
            .map(HourlyWeather::temp)
        holder.weekdayTV.text = sdf.format(dailyWeather.date)
        holder.temperatureTV.text = hourlyWeatherTemp.max()!!.roundToInt()
            .toString()
            .plus(" - ")
//            .plus("\n")
            .plus(hourlyWeatherTemp.min()!!.roundToInt().toString())
            .plus(" ℃")
    }

    override fun getItemCount(): Int {
        return forecastForDayList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            view.setOnClickListener(this)
        }

        val imageView: ImageView = view.findViewById(R.id.imageWeather) as ImageView
        val weekdayTV: TextView = view.findViewById(R.id.weekdayTV)
        val temperatureTV: TextView = view.findViewById(R.id.temperatureTV)


        override fun onClick(v: View) {
            clickListener!!.onItemClick(adapterPosition, v)
        }
    }

    fun setOnItemClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    interface ClickListener {
        fun onItemClick(position: Int, v: View)
    }
}