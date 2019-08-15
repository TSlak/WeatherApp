package com.example.weatherApp.dataBase

import io.realm.RealmObject
import java.text.SimpleDateFormat
import java.util.*

open class CurrentWeather : RealmObject() {
    var temp = Double.NaN
    var maxTemp = Double.NaN
    var minTemp = Double.NaN
    var windSpeed = Double.NaN
    var sunset: Long = 0
    var sunrise: Long = 0
    var description = String()
    var icon = String()
    var windDeg = 0
    var timeZone: Long = 0
    var humidity = 0
    var date = Date()

    fun getSunsetTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date((sunset + timeZone) * 1000));
    }

    fun getSunriseTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date((sunrise + timeZone) * 1000));
    }
}

