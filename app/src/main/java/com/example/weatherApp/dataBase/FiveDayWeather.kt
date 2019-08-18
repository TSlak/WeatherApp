package com.example.weatherApp.dataBase

import com.example.weatherApp.R
import io.realm.RealmList
import io.realm.RealmObject
import java.text.SimpleDateFormat
import java.util.*


open class FiveDayWeather : RealmObject() {
    var cityName: String? = null
    var list: RealmList<FiveDayWeatherList> = RealmList()
    var date: Date = Date()
}

open class FiveDayWeatherList : RealmObject() {
    var temp: Double = 0.0
    var pressure: Double = 0.0
    var humidity: Int = 0
    var dt: Int = 0
    var timezone: Int = 0
    var description: String = ""
    var icon: String = ""
    var windSpeed: Double = 0.0
    fun getWeatherTime(): String {
        return SimpleDateFormat("dd.MM | HH:mm", Locale.getDefault()).format(Date((dt + timezone).toLong() * 1000));
    }

    fun getWeatherImagePath(): Int {
        when (icon) {
            "01n" -> return R.drawable.ic_moon
            "02n" -> return R.drawable.ic_moon_with_grey_cloud
            "03n", "03d" -> return R.drawable.ic_grey_cloud
            "04n", "04d" -> return R.drawable.ic_grey_clouds
            "09n", "09d" -> return R.drawable.ic_grey_clouds_shower_rain
            "10d" -> return R.drawable.ic_grey_cloud_with_small_sun_and_rain
            "10n" -> return R.drawable.ic_grey_cloud_with_moon_and_rain
            "11d", "11n" -> return R.drawable.ic_grey_cloud_lightning
            "13d", "13n" -> return R.drawable.ic_grey_cloud_snow
            "50d", "50n" -> return R.drawable.ic_fog
        }
        return R.drawable.ic_moon
    }
}