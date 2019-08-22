package com.example.weatherApp.dataBase

import io.realm.RealmObject
import java.text.SimpleDateFormat
import java.util.*

open class CurrentWeather : RealmObject() {
    var temp = 0.0
    var cityName = ""
    var maxTemp = 0.0
    var minTemp = 0.0
    var windSpeed = 0.0
    var sunset: Long = 0
    var sunrise: Long = 0
    var description = ""
    var icon = ""
    var windDeg = 0.0
    private var timeZone: Long = 0
    var humidity = 0
    var pressure = 0
    var date = Date()

    fun getSunsetTime(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date((sunset + timeZone) * 1000))
    }

    fun getSunriseTime(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date((sunrise + timeZone) * 1000))
    }
}

