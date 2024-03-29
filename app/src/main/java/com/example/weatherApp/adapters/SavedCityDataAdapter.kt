package com.example.weatherApp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherApp.R
import com.example.weatherApp.dataBase.UserCity
import com.example.weatherApp.helper.IconHelper
import io.realm.Realm
import kotlin.math.roundToInt

class SavedCityDataAdapter(context: Context, var userCityList: List<UserCity>) :
    RecyclerView.Adapter<SavedCityDataAdapter.ViewHolder>() {
    private var clickListener: ClickListener? = null
    var inflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.saved_city_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val realm = Realm.getDefaultInstance()
        val savedCity = realm.copyFromRealm(userCityList[position])
        holder.savedCityTV.text = savedCity.cityName
        if (savedCity.currentWeather != null) {
            holder.tempTv.text = savedCity.currentWeather!!.temp.roundToInt().toString()
                .plus(" ")
                .plus(inflater.context.getString(R.string.celsius))
            holder.savedCityWeatherIcon.setImageResource(IconHelper.getWeatherImageId(savedCity.currentWeather!!.icon))
            holder.humidityTV.text = savedCity.currentWeather!!.humidity.toString()
                .plus(" %")
            holder.pressureTV.text = savedCity.currentWeather!!.pressure.toString()
                .plus(" ")
                .plus(inflater.context.getString(R.string.pressure_unit))
            holder.windSpeedTV.text = savedCity.currentWeather!!.windSpeed.toString()
                .plus(" ")
                .plus(inflater.context.getString(R.string.wind_speed_unit))
        }
    }

    override fun getItemCount(): Int {
        return userCityList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            view.setOnClickListener(this)
        }

        val savedCityTV: TextView = view.findViewById(R.id.cityNameTV)
        val tempTv: TextView = view.findViewById(R.id.tempTV)
        val savedCityWeatherIcon: ImageView = view.findViewById(R.id.savedCityWeatherIcon)
        val pressureTV: TextView = view.findViewById(R.id.pressureTV)
        val humidityTV: TextView = view.findViewById(R.id.humidityTV)
        val windSpeedTV: TextView = view.findViewById(R.id.windSpeedTV)

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