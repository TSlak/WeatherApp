package com.example.weatherApp.dataBase

import com.example.weatherApp.R
import io.realm.RealmObject
import java.text.SimpleDateFormat
import java.util.*

open class CurrentWeather : RealmObject() {
    var temp = 0.0
    var maxTemp = 0.0
    var minTemp = 0.0
    var windSpeed = 0.0
    var sunset: Long = 0
    var sunrise: Long = 0
    var description = ""
    var icon = ""
    var windDeg = 0.0
    var timeZone: Long = 0
    var humidity = 0
    var pressure = 0
    var date = Date()

    fun getSunsetTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date((sunset + timeZone) * 1000));
    }

    fun getSunriseTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date((sunrise + timeZone) * 1000));
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

