package com.example.weatherApp.dataBase

import io.realm.RealmList
import io.realm.RealmObject
import java.text.SimpleDateFormat
import java.util.*


open class FiveDayWeather : RealmObject() {
    var cityName: String? = null
    var dailyWeather : RealmList<DailyWeather> = RealmList()
    var date: Date = Date()
}

open class HourlyWeather : RealmObject() {
    var temp: Double = 0.0
    var pressure: Double = 0.0
    var humidity: Int = 0
    var dt: Int = 0
    var timezone: Int = 0
    var description: String = ""
    var icon: String = ""
    var windSpeed: Double = 0.0
    fun getWeatherTime(): String {
        return SimpleDateFormat("dd.MM | HH:mm", Locale.getDefault()).format(Date((dt + timezone).toLong() * 1000))
    }
}

open class DailyWeather : RealmObject() {
    var date: Date? = null
    var hourlyWeatherList: RealmList<HourlyWeather> = RealmList()
}